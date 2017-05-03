package test;

import java.sql.*;

/**
 * Created by YaoKeQi on 2017/5/1.
 * 11
 */
class MysqlDb {
    String username;
    private String password;
    String nickname;
    private Connection conn;
    private Statement stmt;

    MysqlDb (String username,String password,String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("驱动加载失败");
            e.printStackTrace();
        }
        String url = "jdbc:mysql://127.0.0.1:3306/biliChat?characterEncoding=utf8&useSSL=true";
        try {
            this.conn = DriverManager.getConnection(url,"root","246211");
            this.stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    MysqlDb (String username,String password) {
        this.username = username;
        this.password = password;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("驱动加载失败");
            e.printStackTrace();
        }
        String url = "jdbc:mysql://127.0.0.1:3306/biliChat?characterEncoding=utf8&useSSL=true";
        try {
            this.conn = DriverManager.getConnection(url,"root","246211");
            this.stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.nickname = getNickname();
    }

    boolean isRegistered() {
        String sql = "SELECT username FROM users WHERE username = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1,this.username);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private String getNickname() {
        String sql = "SELECT nickname FROM users WHERE username = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1,this.username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean vertify() {
        String sql = "SELECT password FROM users WHERE username = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1,this.username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String ps = rs.getString(1);
                return this.password.equals(ps);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean register() throws SQLException {
        String sql = "INSERT INTO users VALUES (?,?,?)";
        try {
            conn.setAutoCommit(false);
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1,this.username);
            pst.setString(2,this.password);
            pst.setString(3,this.nickname);
            pst.execute();
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e1) {
                conn.setAutoCommit(true);
                e1.printStackTrace();
            }
            return false;
        }
    }
}
