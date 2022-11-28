package server;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable{

    Socket socket;
    DataInputStream in;
    String name;
    String roomName;
    User user;
    String inString;
    int portNum2; // file transfer를 위한 port


    public Receiver(User user, Socket socket, int portNum2) throws Exception {
        this.user = user;
        this.socket = socket;
        this.portNum2 = portNum2;
        // 접속한 Client로부터 데이터를 읽어들이기 위한 DataInputStream 생성
        in = new DataInputStream(socket.getInputStream());

        inString = in.readUTF(); // #CREATE 등의 명령어를 받음

        String[] splitedInput = inString.split(" ");
        switch (splitedInput[0]) {
            case "#CREATE":
                // TODO 방 생성 명령어 처리
                this.roomName = splitedInput[1];
                this.name = splitedInput[2];
                // 사용자와 생성할 방 이름을 추가해줍니다.
                user.CreateRoom(this.name, socket, roomName);
                break;
            case "#JOIN":
                // TODO 방 참가 명령어 처리
                this.roomName = splitedInput[1];
                this.name = splitedInput[2];
                // 사용자와 참가할 방 이름을 추가해줍니다.
                user.JoinRoom(this.name, socket, roomName);
                break;
            case "#PUT":
                ServerSocket server = new ServerSocket(this.portNum2);
                // 현재 작업중
        }
    }

    public void run() {
        try {
            while(true) {
                String msg = in.readUTF();
                if(msg.equals("#EXIT")) { // "#EXIT"입력 받으면
                    user.ExitResponse(this.name);
                    user.RemoveClient(this.name);
                } else if (msg.equals("#STATUS")) { // "#STATUS"입력 받으면
                    user.SendStatus(name);
                } else {
                    user.sendMsg(msg, name);
                }
            }
        } catch(Exception e) {
            user.RemoveClient(this.name);
        }
    }

}
