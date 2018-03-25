package nettyCase;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by admin on 2018/3/25.
 */
public class NettyServer {

    private int port = 8080;

    private NettyServerHandler serverHandler = new NettyServerHandler();

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        //NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //ServerBootstrap是一个启动NIO服务的辅助启动类，可以在服务中直接使用Channel
            ServerBootstrap bootstrap = new ServerBootstrap();
            //这一步是必须的，如果没有设置group将会报java.lang.IllegalStateException: group not set异常
            bootstrap.group(group);
            //ServerSocketChannel以NIO的selector为基础进行实现的，用来接收新的连接，这里告诉Channel如何获取新的连接
            bootstrap.channel(NioServerSocketChannel.class);
            //指定主机服务的IP端口
            bootstrap.localAddress(new InetSocketAddress(port));
            //事件处理类，会被用来处理已经接收的Channel；可以链接多个处理类
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(serverHandler);
                    /*socketChannel.pipeline().addLast(new ChannelHandlerAdapter());
                    socketChannel.pipeline().addLast(new ChannelHandlerAdapter());*/
                }
            });
            //绑定端口并启动去接收进来的连接
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println(NettyServer.class.getName() + " started and listening for " + future.channel().localAddress());
            //这里会一直等待，直到socket被关闭
            future.channel().closeFuture().sync();
        } finally {
            //释放group资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) {
        try {
            new NettyServer(8888).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
