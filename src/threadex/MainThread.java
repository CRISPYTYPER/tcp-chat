package threadex;

import javax.management.ObjectInstance;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class MainThread extends Thread {
    Socket socket;
    List<Socket> list;
    boolean isFirst;

    public MainThread(Socket socket, List<Socket> list, boolean isFirst) {
        super();
        this.socket = socket;
        this.list = list;
        this.isFirst = isFirst;
    }

    @Override
    public void run() {
        super.run();

        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

    }
}
