package bioCase1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerTest {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) throws IOException, InterruptedException {
        serverStart1();
    }


    private static void serverStart() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("Server is started");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection from [" + socket + "]");

            InputStream is = socket.getInputStream();
            //等待数据准备好
            Thread.sleep(3);
            System.out.println("Length:" + is.available());
            if (is.available() != 0) {
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                System.out.println("Content:" + new String(bytes, "utf-8"));
            } else {
                System.out.println("Content is empty");
            }
            socket.shutdownInput();

            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write("Message is received".getBytes());
            os.flush();
            socket.shutdownOutput();
            if (is != null) {
                try {
                    is.close();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void serverStart1() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("Server is started");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection from [" + socket + "]" + sdf.format(new Date()));

            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            System.out.println("1  " + sdf.format(new Date()));
            //若数据未准备好，则等待一毫秒后再试
//            Thread.sleep(3);
            byte[] bytes = new byte[1024];
            System.out.println("2  " + sdf.format(new Date()));
            while (bis.read(bytes) != -1) {
                System.out.println(new String(bytes) + " | " + sdf.format(new Date()));
            }
//            bis.read(bytes);
            System.out.println("3  " + sdf.format(new Date()));
//            System.out.println("Content:" + new String(bytes));
            socket.shutdownInput();

            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.write("Message is received");
            writer.flush();
            socket.shutdownOutput();
            if (bis != null) {
                try {
                    bis.close();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
