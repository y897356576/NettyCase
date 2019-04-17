package bioCase2;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class ChatRoom implements Runnable {

    public static final String closeSign = "close socket";

    public static final Random random = new Random();
    private static final Long t = random.nextLong();

    private Socket socket;

    public ChatRoom(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedInputStream bis = null;
        PrintWriter writer = null;
        try {
            bis = new BufferedInputStream(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream());

            boolean flag = true;
            while (flag) {
                flag = this.readMessage1(bis, writer);
                Thread.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readMessage(BufferedInputStream bis, PrintWriter writer) throws IOException {
        if(bis.available() == 0) {
            return true;
        }

        byte[] bytes = new byte[bis.available()];
        bis.read(bytes);
        String content = new String(bytes, "utf-8");
        if (content == null || content.trim().length() == 0) {
            return true;
        }

        Message msg = JSON.parseObject(content, Message.class);
        if(msg.getMessage().equals(closeSign)) {
            System.out.println("[" + msg.getFromName() + "] 退出聊天室");
            writer.write("已退出聊天室");
            writer.flush();
            return false;
        } else {
            System.out.println(msg.getFromName() + ": \r\n" + "  " + msg.getMessage());
            writer.write("信息 [" + msg.getMessage() + "] 已接收");
            writer.flush();
        }
        return true;
    }

    private boolean readMessage1(BufferedInputStream bis, PrintWriter writer) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[1024];
        while (bis.read(bytes) == 1024) {
            sb.append(new String(bytes));
            bytes = new byte[1024];
        }
        sb.append(new String(bytes));

        String content = sb.toString();
        if (content == null || content.trim().length() == 0) {
            return true;
        }

        Message msg;
        msg = JSON.parseObject(content, Message.class);
        if(msg.getMessage().equals(closeSign)) {
            System.out.println("[" + msg.getFromName() + "] 退出聊天室");
            writer.write("已退出聊天室");
            writer.flush();
            return false;
        } else {
            System.out.println(msg.getFromName() + ": \r\n" + "  " + msg.getMessage());
            writer.write("信息 [" + msg.getMessage() + "] 已接收");
            writer.flush();
        }
        return true;
    }
}
