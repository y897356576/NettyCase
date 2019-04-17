package nettyCase2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
public class PlainNioServer {

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

        // warp(new byte[len]) 生成一个len容量的包含该当前byte[]内容的ByteBuffer对象
        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());

        for (;;) {
            try {
                selector.select();  //等待需要处理的新事件；阻塞将一直持续到下一个传入事件
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }
            Set<SelectionKey> keys = selector.selectedKeys();  //获取所有接收事件的SelectionKey 实例
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();  //将等会处理的key删除掉，防止下次循环再次处理
                try {
                    if (key.isAcceptable()) {   //检查事件是否是一个新的已经就绪可以被接受的连接（serverChannel获取到新的链接请求）
                        ServerSocketChannel server = (ServerSocketChannel)key.channel();
                        //获取与客户端链接的socket
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        //将客户端链接注册到选择器并设定关注的事件
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());   //将Socket注册到Selector，并设置关注R|W事件
                        System.out.println("Accepted connection from " + client);
                    }
                    if (key.isWritable()) {     //检查套接字是否已经准备好写数据
                        SocketChannel client = (SocketChannel)key.channel();
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) {    //将数据写到已连接的客户端
                                break;
                            }
                        }
                        client.close(); //关闭连接
                    }
                    if (key.isReadable()) {     //检查套接字是否已经准备好读数据

                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new PlainNioServer().serve(6666);
    }

}
