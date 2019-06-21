package nioCase1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 石头 on 2018/9/4.
 */
public class NioServer {

    public void serve(int port) throws IOException {
        // 打开 Server Socket Channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // 配置为非阻塞
        serverChannel.configureBlocking(false);
        // 绑定 Server port
        serverChannel.socket().bind(new InetSocketAddress(port));
        // 创建 Selector
        Selector selector = Selector.open();
        //将ServerSocket注册到Selector，并设置关注ACCEPT事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        serverChannel = null;

        // warp(new byte[len]) 生成一个len容量的包含该当前byte[]内容的ByteBuffer对象
        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());

        for (;;) {
            try {
                int i = selector.select();  //等待需要处理的新事件；阻塞将一直持续到下一个传入事件
                System.out.println("select event num: " + i);
                if (i == 0) continue;
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }
            Set<SelectionKey> keys = selector.selectedKeys();  //获取所有接收事件的SelectionKey 实例
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();  //将等会处理的key删除掉，防止下次循环再次处理
                if (!key.isValid()) {
                    continue;
                }

                System.out.println("EVENT: " + (key.isAcceptable() ? "accept" : key.isReadable() ? "read" : key.isWritable() ? "write" : "other"));
                try {
                    if (key.isAcceptable()) {   //检查事件是否是一个新的已经就绪可以被接受的连接（serverChannel获取到新的链接请求）
                        SelectKeyHandler.doAccept(selector, key);
                    }
                    if (key.isReadable()) {     //检查套接字是否已经准备好读数据
                        SelectKeyHandler.doRead(key);
                    }
                    /*if (key.isWritable()) {     //检查套接字是否已经准备好写数据（只要通道未堵塞，则一直为true）
                        SelectKeyHandler.doWrite(key, "contentTest contentTest contentTest");
                    }*/
                } catch (IOException ex) {
                    key.cancel();
                    ex.printStackTrace();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                        cex.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioServer().serve(6666);
    }

}
