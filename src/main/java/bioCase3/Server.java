package bioCase3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Slf4j
public class Server {

    public static final String host = "127.0.0.1";
    public static final int port = 6666;

    private static DatagramSocket datagramSocket = null;

    public static void serverStart(int port) throws IOException {
        //1.创建数据报套接字
        if (datagramSocket == null) {
            datagramSocket = new DatagramSocket(port);
        }

        while (true) {
            //2.创建一个数据报包
            DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);

            //3.调用receive方法接收数据包（此处阻塞，直到客户端调用 datagramSocket.send() ）
            datagramSocket.receive(datagramPacket);
            new Thread(new ChatRoom(datagramPacket)).start();
        }

        //5.释放资源
        //datagramSocket.close();
    }

    public static void main(String[] args) throws IOException {
        Server.serverStart(port);
    }

}