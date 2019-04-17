package bioCase3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    public static void main(String[] args) {
        for (int i=1; i <= 10; i++) {
            sendMessage(6666 + i, "hello, this is content.");
        }
    }

    private static void sendMessage(int port, String str) {
        try {
            //1.创建数据报套接字
            DatagramSocket datagramSocket = new DatagramSocket(port);

            //2.创建数据报包用于封装数据和目标地址
            byte[] content = str.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(content, content.length, InetAddress.getLocalHost(), 6666);

            //3.调用send方法进行发送数据（此过程后，服务端 receive() 处接收到数据）
            datagramSocket.send(datagramPacket);

            //4.释放资源
            datagramSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
