package main;

public class Client {
    public static void main(String[] args) {
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
    }
}