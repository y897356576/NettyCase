package nettyCase;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Created by admin on 2018/3/25.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ByteBuf message;

    public NettyClientHandler() {
        message = this.stringToByteBuf("query time order");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");
        ctx.writeAndFlush(message);
        //分三次发送，服务端可能是统一读取，也可能是分批读取
        /*ctx.writeAndFlush(this.stringToByteBuf("query "));
        ctx.writeAndFlush(this.stringToByteBuf("time "));
        ctx.writeAndFlush(this.stringToByteBuf("order"));*/
    }

    protected void messageReceived(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        String message = byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("message receiver : " + message);

        if (message.contains("online time") || message.contains("Bad request")) {
            //读取到消息后主动关闭连接
            //ctx.close();
            //向服务端发送消息，通知服务端关闭连接
            ctx.writeAndFlush(this.stringToByteBuf("please close the connection"));
        }
    }

    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        System.out.println("channel reader : " + message);
        //读取到消息后主动关闭连接
        if (message.contains("online time") || message.contains("Bad request")) {
            ctx.close();
        }
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "：出现异常！" + cause.getMessage());
        ctx.close();
    }

    private ByteBuf stringToByteBuf(String str) {
        byte[] bytes = str.getBytes();
        ByteBuf buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }

}
