package nettyCase2.cases.case_2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * Created by 石头 on 2018/9/3.
 */
@ChannelHandler.Sharable    //标示一个ChannelHandler可以被多个Channel安全地共享
public class EchoServerHandler extends ChannelHandlerAdapter {

//    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        ctx.write(in);  //将接收到的消息写给发送者，而不冲刷出站消息
    }

//    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);  //将未决消息冲刷到远程节点，并且关闭该Channel
    }

//    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("主机：" + ctx.channel().remoteAddress()+ " 出现异常；" + cause.getMessage());
        ctx.close();    //关闭该Channel
    }
}
