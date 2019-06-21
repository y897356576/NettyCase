package nettyCase1.cases.case_1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2018/3/25.
 */
public class NettyServer {

    private int port = 8080;

    private NettyServerHandler serverHandler = new NettyServerHandler();

    public NettyServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            new NettyServer(8888).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException {
        //Netty会为每个Channel分配一个EventLoop，用以注册感兴趣的事件、将事件派发给ChannelHandler、安排进一步的动作。

        //NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器，可生成 EventLoop
        //EventLoop 本身只由一个线程驱动，其处理了一个Channel的所有I/O事件
        EventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        EventLoopGroup group = new NioEventLoopGroup(100);
        try {
            //ServerBootstrap是一个启动NIO服务的辅助启动类，可以在服务中直接使用Channel
            ServerBootstrap bootstrap = new ServerBootstrap();
            //这一步是必须的，如果没有设置group将会报java.lang.IllegalStateException: group not set异常
            bootstrap.group(bossGroup, group);
            //ServerSocketChannel以NIO的selector为基础进行实现的，用来接收新的连接，这里告诉Channel如何获取新的连接
            bootstrap.channel(NioServerSocketChannel.class);
            //指定主机服务的IP端口
//            bootstrap.localAddress(new InetSocketAddress(port));

            /*bootstrap.handler(new ChannelHandler() {
               // bossGroup的selector注册处理accept()事件，accept后生成的socket注册到workGroup的selector上；接收连接(accept)时的处理
            });*/

            //事件处理类，会被用来处理已经接收的Channel；可以链接多个处理类
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    System.out.println("\r\nSocketChannel[" + this + "] provide services");
                    //Netty心跳机制，重写handler的userEventTriggered方法
                    socketChannel.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                    //Netty处理tcp黏包问题（基于分隔符）
                    socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//                    socketChannel.pipeline().addLast(new LineBasedFrameDecoder(2048));
                    socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
//                    socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                    socketChannel.pipeline().addLast(serverHandler);
                    /*socketChannel.pipeline().addLast(new ChannelHandlerAdapter());
                    socketChannel.pipeline().addLast(new ChannelHandlerAdapter());*/
                }
            });
            //绑定端口并启动去接收进来的连接
            ChannelFuture future = bootstrap.bind("127.0.0.1", port).sync();
            System.out.println(NettyServer.class.getName() + " started and listening for " + future.channel().localAddress());
            //closeFuture()开启一个监听器，监听future的渠道是否关闭（这里会一直等待，直到channel被关闭）
            future.channel().closeFuture().sync();
        } finally {
            //释放group资源
            bossGroup.shutdownGracefully().sync();
            group.shutdownGracefully().sync();
        }
    }

}
