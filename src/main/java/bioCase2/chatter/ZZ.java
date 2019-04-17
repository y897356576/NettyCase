package bioCase2.chatter;

import bioCase2.ChatRoom;
import bioCase2.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ZZ extends Chatter {

    public ZZ() {
        super("Zhang Zhang");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Chatter zz = new ZZ();
        zz.enterChatRoom(Server.host, Server.port);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String temp;
        while (true) {
            temp = reader.readLine();
            if(temp == null) {
                continue;
            }
            if(temp.equals(ChatRoom.closeSign)) {
                break;
            }
            zz.sendMessage(temp);
            Thread.sleep(500);
        }
        zz.quitCharRoom();
    }

}
