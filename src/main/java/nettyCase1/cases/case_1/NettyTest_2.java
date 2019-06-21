package nettyCase1.cases.case_1;

/**
 * Created by admin on 2018/3/25.
 */
public class NettyTest_2 {

    public static void main(String[] args) {
        try {
            new NettyClient("127.0.0.1", 8888).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
