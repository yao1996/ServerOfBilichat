package test;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created by YaoKeQi on 2017/4/26.
 * 11
 */
public class sendMsgThread implements Runnable{
    private Socket socket;
    private Vector<User> users;
    private boolean run;
    private User user;

    sendMsgThread(Socket socket,Vector<User> users) {
        this.socket = socket;
        this.users = users;
    }
    @Override
    public void run() {
        run = true;
        while (run) {
            DataOutputStream dos;
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                int type =  dis.readByte();
                switch (type) {
                    case 0://登录
                        sendLoginInfo(dis,dos);
                        break;
                    case 1://注册
                        sendRegisterInfo(dis,dos);
                        break;
                    case 2://聊天
                        sendChatMsg(dis);
                        break;
                    case 3://退出
                        getExitInfo();
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                run = false;
                users.removeIf(o -> o.equals(user));
                sendUsers(this.user.nickname,false);
            }
        }
    }

    private boolean getLoginInfo(DataInputStream dis) {
        int i;
        try {
            i = dis.readInt();
            int j = dis.readInt();
            byte[] usernameB = new byte[i];
            dis.readFully(usernameB);
            String username = new String(usernameB);
            byte[] passwordB = new byte[j];
            dis.readFully(passwordB);
            String password = new String(passwordB);
            MysqlDb db = new MysqlDb(username,password);
            User user = new User(socket,db.nickname,db.username);
            if (db.vertify()) {
                for (User user1 : users) {
                    if (user1.username.equals(user.username)) {
                        return false;
                    }
                }
                this.user = user;
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            run = false;
        }
        return false;
    }

    private void sendLoginInfo(DataInputStream dis, DataOutputStream dos) {
        try {
            int type = 0;
            dos.writeByte(type);
            int isSucceeded;
            if (getLoginInfo(dis)) {
                int num = users.size();
                isSucceeded = 1;
                dos.writeByte(isSucceeded);
                dos.writeByte(num);
                for (User user : users) {
                    dos.writeUTF(user.nickname);
                }
                dos.flush();
                users.add(user);
                sendUsers(this.user.nickname,true);
            }else {
                isSucceeded = 0;
                dos.writeByte(isSucceeded);
                dos.flush();
            }
        } catch (IOException e) {
            run = false;
            users.removeIf(o -> o.equals(user));
            sendUsers(this.user.nickname,false);
        }
    }

    private boolean getRegisterInfo(DataInputStream dis)  {
        try {
            int i = dis.readInt();
            int j = dis.readInt();
            byte[] usernameB = new byte[i];
            dis.readFully(usernameB);
            String username = new String(usernameB);
            byte[] passwordB = new byte[j];
            dis.readFully(passwordB);
            String password = new String(passwordB);
            String nickname = dis.readUTF();
            MysqlDb db = new MysqlDb(username, password, nickname);
            return !db.isRegistered() && db.register();
        } catch (IOException e) {
            run = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendRegisterInfo(DataInputStream dis, DataOutputStream dos) {
        int type = 1;
        try {
            dos.writeByte(type);
            int isSucceeded;
            if (getRegisterInfo(dis)) {
                isSucceeded = 1;
                System.out.println("1");
                dos.writeByte(isSucceeded);
                dos.flush();
            }else {
                isSucceeded = 0;
                dos.writeByte(isSucceeded);
                dos.flush();
            }
        } catch (IOException e) {
            run = false;
        }
    }

    private String getChatMsg(DataInputStream dis) {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            run = false;
            users.removeIf(o -> o.equals(user));
            sendUsers(this.user.nickname,false);
            return null;
        }
    }

    private void sendChatMsg (DataInputStream dis) {
            int type = 2;
            String msg = getChatMsg(dis);
            for (User user:users) {
                try {
                    DataOutputStream dos = new DataOutputStream(user.socket.getOutputStream());
                    if (msg != null) {
                        dos.writeByte(type);
                        Date date = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String dateFormated = dateFormat.format(date);
                        dos.writeUTF(this.user.nickname + "  " + dateFormated);
                        dos.writeUTF(msg);
                        dos.flush();
                    }
                } catch (IOException e) {
                    users.remove(user);
                    sendUsers(this.user.nickname,false);
                    run = false;
                }
            }
    }

    private void getExitInfo () {
        run = false;
        users.removeIf(o -> o.equals(user));
        sendUsers(this.user.nickname,false);
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private void sendUsers (String nickname,boolean b) {
        int type = 4;
        int addOrRemove;
        if (b) {
            addOrRemove = 1;
        }else {
            addOrRemove = 0;
        }
        for (User user : users) {
            try {
                DataOutputStream dos = new DataOutputStream(user.socket.getOutputStream());
                dos.writeByte(type);
                dos.writeByte(addOrRemove);
                dos.writeUTF(nickname);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

