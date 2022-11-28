package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
//    Vector<Room> roomV; // 개설된 대화방 Room-vs(Vector) : 대화방 사용자
    public static int portNum1 = -1; // 서버와 채팅 메시지를 주고 받는 용도로 사용되며, #로 시작하는 명령어 전송에도 이용된다.
    public static int portNum2 = -1; // #PUT, #GET 의 동작만을 위해 사용
    public static void main(String[] args) {
        // 접속한 Client와 통신하기 위한 Socket
        Socket socket;
        // 채팅방에 접속해 있는 Client 관리 객체
        User user = new User();
        // Client 접속을 받기 위한 ServerSocket
        ServerSocket server_socket;

        int count = 0;
        Vector<Thread> threadV = new Vector<>();

        if (args.length != 2) {
            System.out.println("Port 번호를 올바르게 입력해주세요.");
            System.exit(0);
        } else {
            portNum1 = Integer.parseInt(args[0]);
            portNum2 = Integer.parseInt(args[1]);
        }

        try {
            server_socket = new ServerSocket(portNum1);
            //Server의 메인쓰레드는 계속해서 사용자의 접속을 받음
            while(true) {
                socket = server_socket.accept();
                threadV.add(new Thread(new Receiver(user, socket)));
                threadV.get(count).start();
                count++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}