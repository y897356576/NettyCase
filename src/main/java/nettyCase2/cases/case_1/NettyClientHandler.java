package nettyCase2.cases.case_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Created by admin on 2018/3/25.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ByteBuf message;
    private ByteBuf messageLong;
    private String req;

    public NettyClientHandler() {
        message = this.stringToByteBuf("query time order");
        req = "In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. His book w"
                + "ill give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the process "
                + "of configuring and connecting all of Netty’s components to bring your learned about threading models in ge"
                + "neral and Netty’s threading model in particular, whose performance and consistency advantages we discuss"
                + "ed in detail In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. Hi"
                + "s book will give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the"
                + " process of configuring and connecting all of Netty’s components to bring your learned about threading "
                + "models in general and Netty’s threading model in particular, whose performance and consistency advantag"
                + "es we discussed in detailIn this chapter you general, we recommend Java Concurrency in Practice by Bri"
                + "an Goetz. His book will give We’ve reached an exciting point—in the next chapter;the counter is: 1 2222"
                + "sdsa ddasd asdsadas dsadasdas " + System.getProperty("line.separator").getBytes();
        //req += req;
        messageLong = this.stringToByteBuf(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");

        //传入长消息时，可能会发生拆包，服务器将一个包分多次读取
        ctx.writeAndFlush(messageLong);
//        messageLong = this.stringToByteBuf(req);
//        ctx.writeAndFlush(messageLong);

        //分多次发送时，可能会发生粘包，服务端可能一次读取多个包
        /*for(int i = 0; i < 100; i++) {
            ctx.writeAndFlush(this.stringToByteBuf("query time order."));
        }*/
    }

    protected void messageReceived(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        String message = byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("message receiver : " + message);

        /*if (message.contains("online time") || message.contains("Bad request")) {
            //读取到消息后主动关闭连接
            //ctx.close();
            //向服务端发送消息，通知服务端关闭连接
            ctx.writeAndFlush(this.stringToByteBuf("please close the connection"));
        }*/
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
