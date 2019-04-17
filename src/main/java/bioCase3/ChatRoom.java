package bioCase3;

import bioCase2.Message;
import com.alibaba.fastjson.JSON;

import java.net.DatagramPacket;

public class ChatRoom implements Runnable {

    public static final String closeSign = "close socket";

    private DatagramPacket datagramPacket;

    public ChatRoom(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }

    @Override
    public void run() {
        Message msg = JSON.parseObject(new String(datagramPacket.getData()), Message.class);
        if(msg.getMessage().equals(closeSign)) {
            System.out.println("[" + msg.getFromName() + "] 退出聊天室");
        } else {
            System.out.println(msg.getFromName() + ": \r\n" + "  " + msg.getMessage());
        }
    }
}
