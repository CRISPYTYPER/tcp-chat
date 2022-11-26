package server;

import java.util.Vector;

public class Room {
    String roomName; // 채팅방 이름
    int userNum; // 채팅방 참여 인원 수
    User user; // 같은 방에 접속한 Client 정보 저장

    public Room() {
        user = new User();
    }
}
