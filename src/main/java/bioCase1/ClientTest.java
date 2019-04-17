package bioCase1;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        for(int i = 0; i < 1; i++) {
            sendMessage();
        }
    }

    private static void sendMessage() throws IOException, InterruptedException {
        System.out.println("1[打开链接前]  " + ServerTest.sdf.format(new Date()));
        Socket socket = new Socket("localhost", 6666);
        System.out.println("2[打开链接后]  " + ServerTest.sdf.format(new Date()));
        Thread.sleep(3);
        OutputStream os = new BufferedOutputStream(socket.getOutputStream());
        System.out.println("3[获取输出流]  " + ServerTest.sdf.format(new Date()));
        Thread.sleep(3);

//        System.out.println("content:" + new String(req.getBytes()));
        os.write("111".getBytes());
        os.flush();
        System.out.println(ServerTest.sdf.format(new Date()));
        Thread.sleep(3);
        os.write("222".getBytes());
        os.flush();
        System.out.println(ServerTest.sdf.format(new Date()));
        Thread.sleep(3);
        os.write("333".getBytes());
        os.flush();
        System.out.println(ServerTest.sdf.format(new Date()));
        Thread.sleep(3);
        socket.shutdownOutput();
        System.out.println("4[关闭输出流]  " + ServerTest.sdf.format(new Date()));

        InputStream is = socket.getInputStream();
        //等待数据准备好
        Thread.sleep(6);
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        System.out.println("Receive:" + new String(bytes));
        socket.shutdownInput();

        try {
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage1() throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 6666);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        System.out.println("content:" + new String(req.getBytes()));
        writer.write(req);
        writer.flush();
        socket.shutdownOutput();

        //等待数据准备好
        Thread.sleep(6);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //若数据未准备好，则等待一毫秒后再试
        while (!reader.ready()) {
//            System.out.println("客户端等待数据传输 " + Thread.currentThread().getName());
            Thread.sleep(20);
        }

        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = reader.readLine()) != null) {
            sb.append(temp);
        }
        System.out.println(sb.toString());
        socket.shutdownInput();

        try {
            writer.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String req = "In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. His book w"
            + "ill give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the process "
            + "of configuring and connecting all of Netty’s components to bring your learned about threading models in ge"
            + "neral and Netty’s threading model in particular, whose performance and consistency advantages we discuss"
            + "ed in detail In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. Hi"
            + "s book will give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the"
            + " process of configuring and connecting all of Netty’s components to bring your learned about threading "
            + "models in general and Netty’s threading model in particular, whose performance and consistency advantag"
            + "es we discussed in detailIn this chapter you general, we recommend Java Concurrency in Practice by Bri"
            + "an Goetz. His book will give We’ve reached an exciting point—in the next chapter;the counter is: 1 2222\r\n";

}
