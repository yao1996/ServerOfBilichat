package test;

import java.net.Socket;

class User {
    Socket socket;
    String nickname;
    String username;

    User(Socket socket, String nickname ,String username) {
        this.socket = socket;
        this.nickname = nickname;
        this.username = username;
    }
}
