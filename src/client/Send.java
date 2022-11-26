package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class Send implements Runnable{
    DataOutputStream outputStream;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public Send(DataOutputStream out) {
        this.outputStream = out;
    }

    public void run() {
        while(true) {
            try {
                String msg = bufferedReader.readLine(); // 키보드로부터 입력을 받음
                outputStream.writeUTF(msg); // 서버로 전송
            } catch (Exception e) {}
        }
    }
}
