package nettyCase2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by 石头 on 2018/9/4.
 */
@Slf4j
public class PlainBioServer {

    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port); //将服务器绑定到指定端口
        try {
            for (;;) {
                final Socket clientSocket = socket.accept();    //接受连接，无连接则阻塞
                //System.out.println("Accepted connection from [" + clientSocket + "]");
                new Thread(new Runnable() {     //创建一个新的线程来处理该连接
                    @Override
                    public void run() {
                        InputStream in;
                        OutputStream out;
                        try {
                            //等待数据加载
                            //Thread.sleep(2);
                            in = clientSocket.getInputStream();
                            byte[] bytes = new byte[1024];
                            int len;
                            StringBuilder sb = new StringBuilder();
                            //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
                            while ((len = in.read(bytes)) != -1) {
                                sb.append(new String(bytes, 0, len, "UTF-8"));  // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                            }
                            System.out.println("Get message from client: " + sb);

                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                            clientSocket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new PlainBioServer().serve(6666);
    }

}
