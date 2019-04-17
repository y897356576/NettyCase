package nettyCase2.cases.case_2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Created by 石头 on 2018/9/3.
 */
public class EchoClient2Handler extends SimpleChannelInboundHandler<ByteBuf> {
    //SimpleChannelInboundHandler源码中已经自动释放了msg的资源，所以如果你保存获取的信息的引用，是无效的
    //此处会将接收的信息转为ByteBuf形式，后续Handler也需要使用ButeBuf形式接收参数
    public EchoClient2Handler() {
    }
    public EchoClient2Handler(Boolean autoRelease) {
        super(autoRelease); //设定是否自动释放msg资源
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("EchoClient2Handler channelActive");
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks 2 !", CharsetUtil.UTF_8));
    }
    @Override
    //链接关闭时会调用
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoClient2Handler channelInactive");
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
        System.out.println("Client_2 received: " + msg.toString(CharsetUtil.UTF_8));
        msg.setByte(0, (byte)'B');
        try {
            msg.writeBytes(" client_2 append.".getBytes("utf-8"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client_2 update: " + msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Exception is:\r\n");
        cause.printStackTrace();
        ctx.close();
    }

}