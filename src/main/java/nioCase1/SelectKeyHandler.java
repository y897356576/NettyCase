package nioCase1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SelectKeyHandler {

    public static void doAccept(Selector selector, SelectionKey selectionKey) throws IOException {
        // 从 selectionKey 中获取当前 key 对应的 channel
        ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();
        // 获取与客户端链接的socket
        SocketChannel client = server.accept();
        // 配置为非阻塞
        client.configureBlocking(false);
        // 将客户端链接注册到选择器并设定关注的事件（ByteBuffer分配是一个昂贵的操作，起码在一个channel内公用一个）
        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
//        client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE, ByteBuffer.allocate(1024)); //不需要注册write事件，只要渠道通畅，write事件一直为true
        System.out.println("Accepted connection from " + client);
    }

    public static void doConnect(Selector selector, SelectionKey selectionKey) throws IOException {
        // 从 selectionKey 中获取当前 key 对应的 channel
        SocketChannel client = (SocketChannel)selectionKey.channel();

        // 是否正在进行连接操作
        if (!client.isConnectionPending()) {
            return;
        }
        //未连接成功则自旋等待
        while (!client.finishConnect()) {
        }

        // 将客户端链接注册到选择器并设定关注的事件
        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        System.out.println("connected with server");
    }

    public static void doRead(SelectionKey selectionKey) throws IOException {
        SocketChannel client = (SocketChannel)selectionKey.channel();
        ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment(); //从selectKey的附件中取出ByteBuffer实现Buffer的复用
        byteBuffer.clear();
        String content = "";
        //注意：数据流内没有数据时，read()返回的t为0
        //      数据流被客户端正常关闭时，read()返回的t为-1
        //      客户端正常关闭时，selector.selectedKeys()会一直获取到此客户端的可读事件，必须从服务端将通道关闭
        //      t == -1 (客户端关闭)，服务端需要手动关闭此客户端的渠道链接
        int t;
        byte[] bytes;
        while ( (t = client.read(byteBuffer)) != -1 && t != 0) {
            byteBuffer.flip();  //准备读取ByteBuffer

            if(byteBuffer.limit() == byteBuffer.capacity()) {
                content = new String(byteBuffer.array(), "utf-8");
            } else {
                bytes = new byte[t];
                byteBuffer.get(bytes);
                content = new String(bytes, "utf-8");
            }

            System.out.println("read content: " + content);
            byteBuffer.clear(); //将position、limit重置，但是不清空内容，byteBuffer.array()依旧可以拿到所有内容
        }

        if (t == -1 || content.equals("close socket")) {
            selectionKey.cancel();
            selectionKey.channel().close();
        }
    }

    public static void doWrite(SelectionKey selectionKey, String content) throws IOException {
        SocketChannel client = (SocketChannel)selectionKey.channel();
        ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
        byte[] bytes = content.getBytes();
        //此处未处理byteBuffer存储内容溢出（溢出会报错 BufferOverflowException ）
        byteBuffer.put(bytes);
        byteBuffer.flip();
        client.write(byteBuffer);

        System.out.println("send content: " + new String(byteBuffer.array()));
    }

    public static void main(String[] args) {
        String content = "12345678901";

        byte[] bytes = content.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        byte[] bs = new byte[3];
        byteBuffer.get(bs);

        System.out.println(new String(bs));
    }

}
