package server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class User {
    HashMap<String, DataOutputStream> clientmap = new HashMap<String, DataOutputStream>(); // <유저명, DataOutputStream>
    HashMap<String, String> roomMap = new HashMap<String, String>(); // <유저명, 채팅방명>

    public synchronized void CreateRoom(String name, Socket socket, String roomName) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            if (!roomMap.containsValue(roomName)) {
                AddClient(name, socket, roomName);
                out.writeUTF("Server : " + roomName + " 생성 후 입장 성공!");
            } else { // 이미 방이 있으면
                out.writeUTF("#401");// Server : 이미 존재하는 채팅방입니다!
//                socket.close(); // 이거 해도 되나?
            }
        } catch (Exception e) {
        }
    }

    public synchronized void JoinRoom(String name, Socket socket, String roomName) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            if (roomMap.containsValue(roomName)) {
                AddClient(name, socket, roomName);
                out.writeUTF("Server : " + roomName + " 입장 성공!");
            } else { // 방이 없으면
                out.writeUTF("#402"); // Server : 존재하지 않는 채팅방입니다!
//                socket.close(); // 이거 해도 되나?

            }
        } catch (Exception e) {
        }
    }

    public synchronized void AddClient(String name, Socket socket, String roomName) {
        try {
            clientmap.put(name, new DataOutputStream(socket.getOutputStream())); // <유저이름, outputstream>
            roomMap.put(name, roomName); // <유저이름, 방이름>
            sendMsg(name + "님이 입장하셨습니다.", "Server", name);
            System.out.println("전체 채팅방 참여 인원 : " + clientmap.size());
        } catch (Exception e) {
        }
    }

    public synchronized void SendStatus(String name) {
        try {
            DataOutputStream out = clientmap.get(name);
            String roomName = roomMap.get(name);
            List<String> usersInRoom = new ArrayList<String>();
            for (HashMap.Entry<String, String> entry : roomMap.entrySet()) {
                if (entry.getValue().equals(roomName)) {
                    usersInRoom.add(entry.getKey());
                }
            }
            out.writeUTF("Server : 현재 방 이름 -> " + roomName);
            out.writeUTF("Server : 현재 구성원 -> " + usersInRoom.toString());
        } catch (Exception e) {
        }
    }

    public synchronized void RemoveClient(String name) {
        try {
            clientmap.remove(name);
            sendMsg(name + "님이 퇴장하셨습니다.", "Server", name);
            roomMap.remove(name);
            System.out.println("전체 채팅방 참여 인원 : " + clientmap.size());
        } catch (Exception e) {
        }
    }

    public synchronized void ExitResponse(String name) {
        try {
            DataOutputStream out = clientmap.get(name);
            out.writeUTF("#EXIT");
        } catch (Exception e) {
        }
    }


    public synchronized void sendMsg(String msg, String name) throws Exception {
        String roomName = roomMap.get(name); // 유저가 속한 방 이름 가져오기
        Iterator iterator = clientmap.keySet().iterator();
        while (iterator.hasNext()) {
            String clientname = (String) iterator.next();
            if (roomMap.get(clientname).equals(roomName)) { // 유저가 속한 방의 사람들에게만 보냄
                if (!name.equals(clientname)) { // 메시지 송신자에게는 보내지 않음 &&
                    clientmap.get(clientname).writeUTF(name + " : " + msg);
                }
            }
        }
    }

    public synchronized void sendMsg(String msg, String serverName, String userName) throws Exception {
        String roomName = roomMap.get(userName); // 유저가 속한 방 이름 가져오기
        Iterator iterator = clientmap.keySet().iterator();
        while (iterator.hasNext()) {
            String clientname = (String) iterator.next();
            if (roomMap.get(clientname).equals(roomName)) { // 유저가 속한 방의 사람들에게만 보냄
                if (!userName.equals(clientname)) { // 메시지 송신자에게는 보내지 않음 &&
                    clientmap.get(clientname).writeUTF(serverName + " : " + msg);
                }
            }
        }
    }

}
