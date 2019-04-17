package nettyCase2.cases.case_2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;

/**
 * Created by 石头 on 2018/9/3.
 */
public class EchoServer {

    private final int port;
    private final EchoServerHandler serverHandler = new EchoServerHandler();

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        /*if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
        }*/
        new EchoServer(8888).start();   //调用服务器的start()方法
    }

    public void start() throws Exception {
        //NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器，可生成 EventLoop
        //Netty会为每个Channel分配一个EventLoop，用以注册感兴趣的事件、将事件派发给ChannelHandler、安排进一步的动作
        //EventLoop 本身只由一个线程驱动，其处理了一个Channel的所有I/O事件
        //（Netty基于NIO，因此用NioEventLoopGroup接受和处理新的连接，并指定使用NioServerSocketChannel类型的Channel）
        EventLoopGroup group = new NioEventLoopGroup(); //创建EventLoopGroup
        try {
            ServerBootstrap b = new ServerBootstrap();  //创建ServerBootstrap
            b.group(group);
            b.channel(NioServerSocketChannel.class);    //指定使用NIO传输的Channel
            b.localAddress(new InetSocketAddress(port));    //服务器绑定到这个地址以监听新的连接请求

            //一个新的连接被接受时，一个新的子Channel将会被创建，
            //而ChannelInitializer 将会把一个你的 EchoServerHandler 的实例添加到该Channel的ChannelPipeline中。
            b.childHandler(new ChannelInitializer<SocketChannel>() {    //添加一个EchoServerHandler到子Channel的ChannelPipeline
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("SocketChannel[" + this + "] provide services for you.");
                            ch.pipeline().addFirst(serverHandler);  //EchoServerHandler被标注为@Shareable，所以我们可以总是使用同样的实例
                        }
                    });

            ChannelFuture f = b.bind();  //异步地绑定服务器
            f.sync();   //调用sync()方法阻塞等待直到绑定完成
            ChannelFuture fc = f.channel().closeFuture();   //获取Channel的CloseFuture（关闭Channel）
            fc.sync();   //阻塞当前线程直到Channel关闭完成
        } finally {
            Future f = group.shutdownGracefully();  //异步关闭EventLoopGroup，释放所有的资源，包括所有被创建的线程
            f.sync();   //阻塞等待，直到异步关闭EventLoopGroup关闭完成
        }
    }

}
