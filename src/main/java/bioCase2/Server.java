package bioCase2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class Server {
    public static final String host = "127.0.0.1";
    public static final int port = 6666;

    private static ServerSocket serverSocket = null;

    public static void serverStart(int port) throws IOException {
        if(serverSocket == null) {
            serverSocket = new ServerSocket(port);
        }

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ChatRoom(socket)).start();
        }
    }

    public static void main(String[] args) throws IOException {
        Server.serverStart(port);
    }

}