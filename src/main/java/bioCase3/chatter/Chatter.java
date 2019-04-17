package bioCase3.chatter;

import bioCase2.Message;
import bioCase3.ChatRoom;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Chatter {

    private String name;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    public Chatter(String name, int selfPort) {
        this.name = name;
        try {
            //初始化数据报套接字（只记录发送的本地端口）
            datagramSocket = new DatagramSocket(selfPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void enterChatRoom(String hostAddress, int port) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(hostAddress);
        //初始化数据报包（目标地点存放在数据报包内）
        datagramPacket = new DatagramPacket("".getBytes(), 0, inetAddress, port);
    }

    public void sendMessage(String msg) {
        if (msg == null || msg.trim().length() == 0) {
            return;
        }
        Message message = new Message();
        message.setFromName(this.name);
        message.setMessage(msg);

        //设置数据报包内容
        datagramPacket.setData(JSON.toJSONString(message).getBytes());
        try {
            //通过数据报套接字发送数据报
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quitCharRoom() {
        this.sendMessage(ChatRoom.closeSign);
        //释放资源
        datagramPacket = null;
        if (datagramSocket != null) {
            datagramSocket.close();
        }
    }

}
