package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static int portNum1 = -1; // 서버와 채팅 메시지를 주고 받는 용도로 사용되며, #로 시작하는 명령어 전송에도 이용된다.
    public static int portNum2 = -1; // #PUT, #GET 의 동작만을 위해 사용
    public static List<ServerSocket> severList = new ArrayList<ServerSocket>(); // 채팅 서버 리스트
    public static List<String> roomNameList = new ArrayList<String>(); // 방 이름 리스트
    public static List<Integer> port = new ArrayList<Integer>(); // 포트 번호
    public static List<List<Socket>> clientSocketList = new ArrayList<List<Socket>>(); // 채팅 서버에 들어온 클라이언트 소켓 리스트


    public static void main(String[] args) {
        Socket clientSocket = null;
        boolean isFirst = true;
        if (args.length != 2) {
            System.out.println("Port 번호를 올바르게 입력해주세요.");
            System.exit(0);
        } else {
            portNum1 = Integer.parseInt(args[0]);
            portNum2 = Integer.parseInt(args[1]);
        }

        try {
            ServerSocket serverSocket = new ServerSocket(portNum1);

            List<Socket> list = new ArrayList<Socket>();

            while (true) {
                // 서버는 항상 열려있어야 함
                System.out.println("접속 대기중...");
                clientSocket = serverSocket.accept();
                list.add(clientSocket);

                System.out.println("Client IP: " + clientSocket.getInetAddress() + "Port: " + clientSocket.getPort());

                // 아래 부분은 접속 된 소켓을 통해 소통을 해야함 - 스레드로 이동해야함.
                new MainThread(clientSocket, list, isFirst).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}