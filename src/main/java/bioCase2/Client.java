package bioCase2;

import bioCase2.chatter.Chatter;

import java.io.IOException;
import java.security.SecureRandom;

public class Client {

    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) throws IOException {

        for (int i = 0; i < 10; i++) {
            final int j = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Chatter chatter = new Chatter("name-" + j);
                    try {
                        chatter.enterChatRoom(Server.host, Server.port);
                        for (int i = 0; i < 10; i++) {
                            chatter.sendMessage("message:" + random.nextInt(100));
                        }
                        chatter.quitCharRoom();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

}
