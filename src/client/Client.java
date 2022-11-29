package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket socket; // Server와 통신하기 위한 Socket
        Socket socket2; // Server와 파일을 주고 받기 위한 Socket
        DataInputStream serverInStream = null; // Server로부터 데이터를 읽어들이기 위한 입력스트림
        DataOutputStream outputStream;
        boolean isCommand = false;
        String commandPhrase = "";
        String ipAddress = "";
        String fileName = "";
        int portNum1 = -1;
        int portNum2 = -1; // 포트번호 초기화
        byte[] buffer = new byte[65536];

        if (args.length != 3) {
            System.out.println("Port 번호를 올바르게 입력해주세요.");
            System.exit(0);
        } else {
            ipAddress = args[0];
            portNum1 = Integer.parseInt(args[1]);
            portNum2 = Integer.parseInt(args[2]);
        }

        String inputString;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            while ((inputString = reader.readLine()) != null) {
                if (inputString.length() == 0) {
                    continue;
                }
                String[] splitedInput = inputString.split(" ");

//                '#'확인되면 isCommand flag true로 바꾸기
                isCommand = (splitedInput[0].charAt(0) == '#') ? true : false;

                if (isCommand == true) {
//                    commandPhrase는 #지운 명령어 자체(e.g. #JOIN -> JOIN)
                    commandPhrase = splitedInput[0].substring(1);

                    String roomName;
                    String userName;
                    String response;
                    boolean isError;
                    switch (commandPhrase) {
                        case "CREATE":
                            if (splitedInput.length != 3) {
                                System.out.println("#CREATE (생성할 채팅방의 이름) (사용자 이름)");
                                System.out.println("위와 같은 형식으로 입력해주세요.");
                                break;
                            }
                            roomName = splitedInput[1];
                            userName = splitedInput[2];

                            socket = new Socket(InetAddress.getByName(ipAddress), portNum1); // 서버로 접속
                            serverInStream = new DataInputStream(socket.getInputStream());
                            outputStream = new DataOutputStream(socket.getOutputStream());
                            String createReq = "#CREATE " + roomName + " " + userName;
                            // 서버로 방 생성 명령 전송
                            outputStream.writeUTF(createReq);
                            // 사용자가 채팅 내용을 입력 및 서버로 전송하기 위한 쓰레드 생성 및 시작
                            response = serverInStream.readUTF();
                            isError = (response.equals("#401") || response.equals("402"));
                            if (!isError) {
                                System.out.println(response); // "클라이언트에 생성 및 입장 성공 메시지 출력"
                                Thread th = new Thread(new Send(outputStream));
                                th.start();
                                try {
                                    // 클라이언트의 메인 쓰레드는 서버로부터 데이터 읽어들이는 것만 반복.
                                    while (true) {
                                        String receivedString = serverInStream.readUTF();
                                        if (receivedString.equals("#EXIT")) {
                                            break; // server로부터 "#EXIT"메시지가 오면 break
                                        }
                                        System.out.println(receivedString);
                                    }
                                } catch (IOException e) {
                                }
                            } else if (response.equals("#401")) {
                                System.out.println("Server : 이미 존재하는 채팅방입니다!");
                            } else {
                                System.out.println("알 수 없는 에러 - Client.java");
                            }
                            break;
                        case "JOIN":
                            if (splitedInput.length != 3) {
                                System.out.println("#JOIN (참여할 채팅방의 이름) (사용자 이름)");
                                System.out.println("위와 같은 형식으로 입력해주세요.");
                                break;
                            }
                            roomName = splitedInput[1];
                            userName = splitedInput[2];

                            socket = new Socket(InetAddress.getByName(ipAddress), portNum1); // 서버로 접속
                            serverInStream = new DataInputStream(socket.getInputStream());
                            outputStream = new DataOutputStream(socket.getOutputStream());
                            String joinReq = "#JOIN " + roomName + " " + userName;
                            // 서버로 방 생성 명령 전송
                            outputStream.writeUTF(joinReq);
                            // 사용자가 채팅 내용을 입력 및 서버로 전송하기 위한 쓰레드 생성 및 시작
                            response = serverInStream.readUTF();
                            isError = (response.equals("#401") || response.equals("#402"));
                            if (!isError) {
                                System.out.println(response); // "클라이언트에 입장성공 메시지 출력"
                                Thread th = new Thread(new Send(outputStream));
                                th.start();
                                try {
                                    // 클라이언트의 메인 쓰레드는 서버로부터 데이터 읽어들이는 것만 반복.
                                    while (true) {
                                        String receivedString = serverInStream.readUTF();
                                        if (receivedString.equals("#EXIT")) {
                                            break; // server로부터 "#EXIT"메시지가 오면 break
                                        }
                                        System.out.println(receivedString);
                                    }
                                } catch (IOException e) {
                                }
                            } else if (response.equals("#402")) {
                                System.out.println("Server : 존재하지 않는 채팅방입니다!");
                            } else {
                                System.out.println("알 수 없는 에러 - Client.java");
                            }
                            break;
                        case "PUT":
                            if (splitedInput.length != 2) {
                                System.out.println("#PUT (파일명)");
                                System.out.println("위와 같은 형식으로 입력해주세요.");
                                break;
                            }
                            fileName = splitedInput[1];

                            File file = new File("./src/client/files/" + fileName); //테스트용
//                            File file = new File("./client/files/" + fileName); //제출용
                            if (!file.exists()) {
                                System.out.println("Error : 해당 파일이 존재하지 않습니다!");
                                break;
                            }


                            try {
                                socket = new Socket(InetAddress.getByName(ipAddress), portNum1);
                                serverInStream = new DataInputStream(socket.getInputStream());
                                outputStream = new DataOutputStream(socket.getOutputStream());
                                String putReq = "#PUT " + fileName;
                                // 서버로 파일 수신 대기 준비 명령 전송
                                outputStream.writeUTF(putReq);
                                String responseMsg = serverInStream.readUTF();
                                if (!responseMsg.equals("#200")) {
                                    System.out.println("Server : 문제가 발생하였습니다");
                                    break;
                                }
                                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                                socket2 = new Socket(InetAddress.getByName(ipAddress), portNum2);
                                if (!socket2.isConnected()) {
                                    System.out.println("Server : 소켓 연결에 문제가 발생하였습니다!");
                                    break;
                                }
                                BufferedOutputStream bos = new BufferedOutputStream(socket2.getOutputStream());
                                int readBytes = 0;
                                while ((readBytes = bis.read(buffer)) > 0) {
                                    bos.write(buffer, 0, readBytes);
                                    if (readBytes == 65536)
                                        System.out.print("#");
                                }
                                System.out.println();
                                bos.flush();
                                System.out.println("Server : 서버로 파일 전송이 완료되었습니다!");
                                bos.close();
                                responseMsg = serverInStream.readUTF();
                                System.out.println(responseMsg);
                                bis.close();
                                socket2.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "GET":
                            if (splitedInput.length != 2) {
                                System.out.println("#GET (파일명)");
                                System.out.println("위와 같은 형식으로 입력해주세요.");
                                break;
                            }
                            fileName = splitedInput[1];

                            try {
                                socket = new Socket(InetAddress.getByName(ipAddress), portNum1);
                                serverInStream = new DataInputStream(socket.getInputStream());
                                outputStream = new DataOutputStream(socket.getOutputStream());
                                String getReq = "#GET " + fileName;
                                // 서버로 파일 전송 대기 준비 명령 전송
                                outputStream.writeUTF(getReq);
                                String responseMsg = serverInStream.readUTF();
                                if (responseMsg.equals("404")) {
                                    System.out.println("Server : 서버에 해당 파일이 존재하지 않습니다!");
                                    break;
                                } else if (!responseMsg.equals("#200")) {
                                    System.out.println("Server : 문제가 발생하였습니다");
                                    break;
                                }
                                socket2 = new Socket(InetAddress.getByName(ipAddress), portNum2);
                                if (!socket2.isConnected()) {
                                    System.out.println("Server : 소켓 연결에 문제가 발생하였습니다!");
                                    break;
                                }

                                DataInputStream dis = new DataInputStream(socket2.getInputStream());
                                BufferedInputStream bis = new BufferedInputStream(dis);
                                //테스트용
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./src/client/files/" + fileName)); //클라이언트에 파일을 저장하기 위한 Stream
//                                //제출용
//                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./client/files/" + fileName)); //클라이언트에 파일을 저장하기 위한 Stream
                                int readBytes = 0;
                                while ((readBytes = bis.read(buffer)) > 0) {
                                    bos.write(buffer, 0, readBytes);
                                    if (readBytes == 65536)
                                        System.out.print("#");
                                }
                                System.out.println();
                                bos.flush();
                                bos.close();
                                responseMsg = serverInStream.readUTF();
                                System.out.println(responseMsg);
                                bis.close();
                                socket2.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "STATUS":
                            // 이 부분은 서버에서 처리됨.
                            System.out.println("현재 채팅방에 접속해 있지 않습니다!");
                            break;
                        case "EXIT":
                            // 이 부분은 서버에서 처리됨.
                            System.out.println("현재 채팅방에 접속해 있지 않습니다!");
                            break;
                        default:
                            System.out.println("잘못된 명령어 입력");
                            break;
                    }
                } else { // if (isCommand == false)
                    System.out.println("Sever : 현재 채팅방에 입장해있지 않습니다!");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}