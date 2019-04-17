package bioCase2.chatter;

import bioCase2.ChatRoom;
import bioCase2.Message;
import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.Socket;

public class Chatter {

    public Chatter(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    private Socket socket = null;
    private OutputStream os = null;
    private InputStream is = null;

    public void enterChatRoom(String host, int port) throws IOException, InterruptedException {
        if(socket != null) {
            this.quitCharRoom();
        }
        socket = new Socket(host, port);
        os = new BufferedOutputStream(socket.getOutputStream());
        is = socket.getInputStream();
    }

    public void sendMessage(String msg) throws IOException, InterruptedException {
        if(msg == null || msg.trim().length() == 0) {
            return;
        }

        Message message = new Message();
        message.setFromName(this.name);
        message.setMessage(msg);
        os.write(JSON.toJSONString(message).getBytes());
        os.flush();

        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[1024];
        while (is.read(bytes) == 1024) {
            sb.append(new String(bytes));
            bytes = new byte[1024];
        }
        sb.append(new String(bytes));
        System.out.println(sb.toString());
    }

    public void quitCharRoom() throws IOException, InterruptedException {
        this.sendMessage(ChatRoom.closeSign);
        if(os != null) {
            os.close();
        }
        if(is != null) {
            is.close();
        }
    }

}
