package nioCase1;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {

    public void client(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        OutputStream os = new BufferedOutputStream(socket.getOutputStream());
        os.write(str.getBytes());
        os.flush();
        socket.close();
        System.in.read();
    }

    public void nioClient(String host, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(host, port));

        while (true) {
            int i = selector.select();  //阻塞等待事件
            if (i == 0) {
                continue;
            }

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (!key.isValid()) {
                    continue;
                }

                System.out.println("EVENT: " + (key.isConnectable() ? "connect" : key.isReadable() ? "read" : key.isWritable() ? "write" : "other"));
                try {
                    if (key.isConnectable()) {   //检查链接是否成功建立
                        SelectKeyHandler.doConnect(selector, key);
                    }
                    if (key.isWritable()) {     //检查套接字是否已经准备好写数据
                        SelectKeyHandler.doWrite(key, "contentTest contentTest contentTest");
                    }
                    if (key.isReadable()) {     //检查套接字是否已经准备好读数据
                        SelectKeyHandler.doRead(key);
                    }
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
        new NioClient().client("localhost", 6666);
    }


    String str = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
}
