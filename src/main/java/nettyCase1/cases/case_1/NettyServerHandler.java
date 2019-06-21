package nettyCase1.cases.case_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.text.SimpleDateFormat;

/**
 * Created by admin on 2018/3/25.
 */
@ChannelHandler.Sharable    //标示一个ChannelHandler可以被多个Channel安全地共享，保证线程安全
public class NettyServerHandler extends ChannelHandlerAdapter {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 在与一个客户端连接时触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端与客户端 " + ctx.channel().toString() + " 建立链接\r\n");
        //在与客户端建立连接后发送欢迎消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("服务端向你表示欢迎\r\n".getBytes()));
    }

    /**
     * @param ctx 与客户端通信的通道
     * @param msg 客户端发送的信息主体
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String inMsg = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        System.out.println("服务端收到的客户端 " + ctx.channel().toString() + " 的消息是：" + inMsg);

        //验证消息是否为通知关闭连接
        //this.checkCloseMsg(ctx, inMsg);

        /*String currentTime;
        if ("query time order".equalsIgnoreCase(inMsg)) {
            currentTime = "online time " + sdf.format(new Date());
        } else {
            currentTime = "Bad request";
        }
        ByteBuf responseBuf = Unpooled.copiedBuffer(currentTime.getBytes());
        //通过与客户端的通道回写信息
        ctx.write(responseBuf);*/
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //1、向客户端写入空字符串
        //2、向客户端发送写入的信息
        //3、添加监听，促使客户端关闭链接
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("主机：" + ctx.channel().remoteAddress() + " 出现异常；" + cause.getMessage());
        ctx.close();
    }

    private void checkCloseMsg(ChannelHandlerContext ctx, String msg) {
        if ("please close the connection".equalsIgnoreCase(msg)) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            System.out.println("\r\n服务端与客户端 " + ctx.channel().toString() + " 链接已关闭\r\n-------------------");
        }
    }


    private int loss_connect_time = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                loss_connect_time++;
                System.out.println("5 秒没有接收到客户端的信息了");
                if (loss_connect_time > 2) {
                    System.out.println("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
            }
        } else {
//            super.userEventTriggered(ctx, evt);
        }
    }
}