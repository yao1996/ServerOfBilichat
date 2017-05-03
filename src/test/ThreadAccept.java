package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Vector;

/**
 * Created by YaoKeQi on 2017/4/26.
 * 11
 */
public class ThreadAccept implements Runnable {
    private ServerSocket serverSocket;
    private Vector<User> users;

    ThreadAccept (ServerSocket serverSocket,Vector<User> users) {
        this.serverSocket = serverSocket;
        this.users = users;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Thread sendToAll = new Thread(new sendMsgThread(socket,users));
                sendToAll.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {
            }
        }
    }
}
