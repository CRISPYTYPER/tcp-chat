package main;

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

        if (args.length != 2) {
            System.out.println("Port 번호를 올바르게 입력해주세요.");
            System.exit(0);
        } else {
            portNum1 = Integer.parseInt(args[0]);
            portNum2 = Integer.parseInt(args[1]);
        }
    }
}