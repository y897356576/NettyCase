package nettyCase2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by 石头 on 2018/9/4.
 */
public class NettyNioServer {

    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
        EventLoopGroup group = new NioEventLoopGroup(); //为非阻塞模式使用NioEventLoopGroup
        try {
            ServerBootstrap b = new ServerBootstrap();  //创建ServerBootstrap
            b.group(group)
                    .channel(NioServerSocketChannel.class)  //使用NioEventLoopGroup以允许非阻塞模式
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() { //指定ChannelInitializer，对于每个已接受的连接都调用它创建新的Channel
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ChannelInboundHandlerAdapter() {    //添加一个ChannelInboundHandlerAdapter 以拦截和处理事件
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            ctx.writeAndFlush(buf.duplicate())
                                                    .addListener(ChannelFutureListener.CLOSE);  //将消息写到客户端，并添加ChannelFutureListener，以便消息一被写完就关闭连接
                                        }
                                    });
                        }
                    });
            ChannelFuture f = b.bind().sync();  //绑定服务器以接受连接
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();  //释放所有的资源
        }
    }

}
