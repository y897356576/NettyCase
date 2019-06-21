package nettyCase1.cases.case_2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Created by 石头 on 2018/9/3.
 */
public class EchoClient1Handler extends SimpleChannelInboundHandler<ByteBuf> {
    //SimpleChannelInboundHandler源码中已经自动释放了msg的资源，所以如果你保存获取的信息的引用，是无效的
    //此处会将接收的信息转为ByteBuf形式，后续Handler也需要使用ButeBuf形式接收参数
    public EchoClient1Handler() {
    }
    public EchoClient1Handler(Boolean autoRelease) {
        super(autoRelease); //设定是否自动释放msg资源
    }

    private Integer mark = 0;

    @Override
    /**
     * 打开与服务端连接时调用此方法
     * 通过上下文向服务端写入并冲刷数据，此时数据已被发送至服务器
     * 服务器收到数据并回写数据，回写的数据将会触发客户端的channelRead方法
     * 因为一个channel的所有Handler中的方法都由同一个线程执行，因此Handler中的方法是同步串行执行的
     * 因此需要等待channelActive执行完毕再执行channelRead方法；
     * [注：客户端的IO操作都是由channel绑定的EventLoop完成的，一个EventLoop唯一绑定一个线程]
     */
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("EchoClient1Handler channelActive; mark " + mark);
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks 1 !", CharsetUtil.UTF_8)); //writeAndFlush会将对象的引用计数-1
        ctx.fireChannelActive();    //开启后续Handler的channelActive方法
    }
    @Override
    //链接关闭时会调用
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoClient1Handler channelInactive");
        ctx.fireChannelInactive();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
        System.out.println("EchoClient1Handler channelRead; mark " + mark);
        System.out.println("Client_1 received: " + msg.toString(CharsetUtil.UTF_8));
        msg.setByte(0, (byte)'A');
        try {
            msg.writeBytes(" client_1 append.".getBytes("utf-8"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client_1 update: " + msg.toString(CharsetUtil.UTF_8));
        ctx.fireChannelRead(msg);    //开启后续Handler的channelRead方法
        msg.release();  //手动将资源的计数器-1
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        System.out.println("Exception is:\r\n");
        e.printStackTrace();
        ctx.close();
    }

}