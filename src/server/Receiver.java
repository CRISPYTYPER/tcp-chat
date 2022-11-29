package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {

    Socket socket;
    ServerSocket socket2;
    DataInputStream in;
    String name;
    String roomName;
    User user;
    String inString;
    String fileName; // 전달받을 파일 이름
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
                this.roomName = splitedInput[1];
                this.name = splitedInput[2];
                // 사용자와 생성할 방 이름을 추가해줍니다.
                user.CreateRoom(this.name, socket, roomName);
                break;
            case "#JOIN":
                this.roomName = splitedInput[1];
                this.name = splitedInput[2];
                // 사용자와 참가할 방 이름을 추가해줍니다.
                user.JoinRoom(this.name, socket, roomName);
                break;
            case "#PUT":
                this.fileName = splitedInput[1];
                try {
                    this.socket2 = new ServerSocket(this.portNum2);
                    DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
                    out.writeUTF("#200");
                    Socket socket2 = this.socket2.accept(); // 파일 전송을 위한 소켓 생성


                    InetSocketAddress isaClient = (InetSocketAddress) socket2.getRemoteSocketAddress();
                    System.out.println("A client(" + isaClient.getAddress().getHostAddress() +
                            " is connected. (Port: " + isaClient.getPort() + ")");
                    DataInputStream dis = new DataInputStream(socket2.getInputStream());
                    BufferedInputStream bis = new BufferedInputStream(dis);
                    //테스트용
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./src/server/files/" + fileName)); //서버에 파일을 저장하기 위한 Stream
                    //제출용
//                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./server/files/" + fileName)); //서버에 파일을 저장하기 위한 Stream
                    byte[] buffer = new byte[65536];
                    int readBytes = 0;
                    while ((readBytes = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, readBytes);
                    }
                    bos.flush();
                    System.out.println("파일 수신 성공!");
                    out.writeUTF("Server : 서버에 파일을 성공적으로 저장했습니다!");
                    bos.close();
                    bis.close();
                    this.socket2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "#GET":
                this.fileName = splitedInput[1];
                byte[] buffer = new byte[65536];
                try {
                    this.socket2 = new ServerSocket(this.portNum2);
                    DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
                    //테스트용
                    File file = new File("./src/server/files/" + fileName);
//                    //제출용
//                    File file = new File("./server/files/" + fileName);
                    if (!file.exists()) {
                        System.out.println("Error : 서버에 해당 파일이 존재하지 않습니다!");
                        out.writeUTF("#404");
                        break;
                    }
                    out.writeUTF("#200");
                    Socket socket2 = this.socket2.accept(); // 파일 전송을 위한 소켓 생성

                    InetSocketAddress isaClient = (InetSocketAddress) socket2.getRemoteSocketAddress();
                    System.out.println("A client(" + isaClient.getAddress().getHostAddress() +
                            " is connected. (Port: " + isaClient.getPort() + ")");

                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    BufferedOutputStream bos = new BufferedOutputStream(socket2.getOutputStream());
                    int readBytes = 0;
                    while ((readBytes = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, readBytes);
                    }
                    bos.flush();
                    System.out.println("파일 전송 성공!");
                    out.writeUTF("Server : 클라이언트로 파일을 성공적으로 전송했습니다!");
                    bos.close();
                    bis.close();
                    this.socket2.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        } // switch문 끝
    }

    public void run() {
        try {
            while (true) {
                String msg = in.readUTF();
                if (msg.equals("#EXIT")) { // "#EXIT"입력 받으면
                    user.ExitResponse(this.name);
                    user.RemoveClient(this.name);
                } else if (msg.equals("#STATUS")) { // "#STATUS"입력 받으면
                    user.SendStatus(this.name);
                } else {
                    user.sendMsg(msg, this.name);
                }
            }
        } catch (Exception e) {
            user.RemoveClient(this.name);
        }
    }

}
