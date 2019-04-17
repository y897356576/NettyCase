package nettyCase2.cases.case_2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Timer;

/**
 * 重连检测狗，当发现当前的链路不稳定关闭之后，进行12次重连
 */
@Sharable
public class ConnectionWatchDog extends ChannelHandlerAdapter {

    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;
    private final String host;
    private volatile boolean reconnect = true;
    private int attempts;

    public ConnectionWatchDog(Bootstrap bootstrap, Timer timer, String host, int port, boolean reconnect) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
    }

    /**
     * channel链路每次active的时候，将其连接的次数重新☞ 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("当前链路已经激活了，重连尝试次数重新置为0");
        attempts = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("链接关闭");
        if(reconnect){
            System.out.println("链接关闭，将进行重连");
            if (attempts < 12) {
                attempts++;
                //重连的间隔时间会越来越长
                /*int timeout = 2 << attempts;
                Thread.sleep(timeout);*/
                this.reConnect();
            }
        }
        //ctx.fireChannelInactive();
    }

    private void reConnect() throws Exception {
        ChannelFuture future;
        //bootstrap已经初始化好了，只需要将handler填入就可以了
        synchronized (bootstrap) {
            /*bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });*/
            future = bootstrap.connect(host, port);
        }
        //future对象
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                boolean succeed = f.isSuccess();
                //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
                if (!succeed) {
                    System.out.println("重连失败");
                    f.channel().pipeline().fireChannelInactive();
                } else {
                    System.out.println("重连成功");
                }
            }
        });
    }

}