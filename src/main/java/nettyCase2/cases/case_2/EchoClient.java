package nettyCase2.cases.case_2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;

import java.net.InetSocketAddress;

/**
 * Created by 石头 on 2018/9/3.
 */
public class EchoClient {
    private String host;
    private int port;
    protected final HashedWheelTimer timer = new HashedWheelTimer();

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        /*if (args.length != 2) {
            System.err.println("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);*/
        new EchoClient("127.0.0.1", 8888).start();
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
//            final ConnectionWatchDog watchDog = new ConnectionWatchDog(b, timer, host, port, true);
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast("decoder", new StringDecoder());
//                            ch.pipeline().addLast("encoder", new StringEncoder());
//                            ch.pipeline().addLast(watchDog);
//                            ch.pipeline().addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));

                            ch.pipeline().addLast(new EchoClient1Handler(false));
                            ch.pipeline().addLast(new EchoClient2Handler(false));
                        }
                    });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
