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
                if(msg.charAt(0) == '#') {
                    if(msg.equals("#EXIT")){}
                    else if(msg.equals("#STATUS")){}
                    else {
                        System.out.println("Server : 잘못된 명령어입니다!");
                        continue;
                    }
                }
                outputStream.writeUTF(msg); // 서버로 전송
                if(msg.equals("#EXIT")) {
                    System.out.println("채팅방을 나갑니다..");
                    break;
                }
            } catch (Exception e) {}
        }
    }
}
