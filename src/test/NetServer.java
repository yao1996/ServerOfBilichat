package test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Vector;

/**
 * Created by YaoKeQi on 2017/4/25.
 * 11
 */
public class NetServer {
    public static void main(String[] args) {
        Vector<User> users = new Vector<>();
        try {
            ServerSocket serverSocket;
            serverSocket = new ServerSocket(8888);

            Thread accept = new Thread(new ThreadAccept(serverSocket,users));
            accept.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}