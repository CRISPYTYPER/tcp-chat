package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket socket; // Server와 통신하기 위한 Socket
        DataInputStream serverInStream = null; // Server로부터 데이터를 읽어들이기 위한 입력스트림
        BufferedReader keyboardInStream; // 키보드로부터 읽어들이기 위한 입력스트림
        DataOutputStream outputStream;

        String ipAddress = "";
        int portNum1 = -1;
        int portNum2 = -1; // 포트번호 초기화

        if (args.length != 3) {
            System.out.println("Port 번호를 올바르게 입력해주세요.");
            System.exit(0);
        } else {
            ipAddress = args[0];
            portNum1 = Integer.parseInt(args[1]);
            portNum2 = Integer.parseInt(args[2]);
        }
        try {
            socket = new Socket(InetAddress.getByName(ipAddress), portNum1); // 서버로 접속
            serverInStream = new DataInputStream(socket.getInputStream());
            keyboardInStream = new BufferedReader(new InputStreamReader(System.in));
            outputStream = new DataOutputStream(socket.getOutputStream());

            // 채팅에 사용할 닉네임을 입력받음
            System.out.print("닉네임을 입력해주세요 : ");
            String data = keyboardInStream.readLine();

            // 서버로 닉네임을 전송
            outputStream.writeUTF(data);
            // 사용자가 채팅 내용을 입력 및 서버로 전송하기 위한 쓰레드 생성 및 시작
            Thread th = new Thread(new Send(outputStream));
            th.start();

        } catch (IOException e) {
        }
        try {
            // 클라이언트의 메인 쓰레드는 서버로부터 데이터 읽어들이는 것만 반복.
            while(true) {
                String receivedString = serverInStream.readUTF();
                System.out.println(receivedString);
            }
        }catch (IOException e) {}

    }
}