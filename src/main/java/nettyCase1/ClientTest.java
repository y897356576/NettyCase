package nettyCase1;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        for(int i = 0; i < 10; i++) {
            sendMessage();
        }
    }

    private static void sendMessage() throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 6666);
        OutputStream os = new BufferedOutputStream(socket.getOutputStream());

        String req = "In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. His book w"
                + "ill give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the process "
                + "of configuring and connecting all of Netty’s components to bring your learned about threading models in ge"
                + "neral and Netty’s threading model in particular, whose performance and consistency advantages we discuss"
                + "ed in detail In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. Hi"
                + "s book will give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the"
                + " process of configuring and connecting all of Netty’s components to bring your learned about threading "
                + "models in general and Netty’s threading model in particular, whose performance and consistency advantag"
                + "es we discussed in detailIn this chapter you general, we recommend Java Concurrency in Practice by Bri"
                + "an Goetz. His book will give We’ve reached an exciting point—in the next chapter;the counter is: 1 2222"
                + "sdsa ddasd asdsadas dsadasdas " + System.getProperty("line.separator").getBytes();

        os.write(req.getBytes());
        os.flush();
        socket.shutdownOutput();

        //等待数据加载
        Thread.sleep(5);
        InputStream is = socket.getInputStream();
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        System.out.println("Receive:" + new String(bytes));
        socket.shutdownInput();

        try {
            os.close();
            is.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
