package com.book.store.system.Controllers;

import org.mindrot.jbcrypt.BCrypt;
import org.sqlite.ExtendedCommand.SQLExtension;

import java.sql.*;

import com.book.store.system.Db.Db;
import com.book.store.system.Entities.User;

public class UserController {
    private String userId;
    private String userName;
    private String userType;
    private Connection connection;

    public UserController() {
        this.connection = Db.connect();
    }

    public int register(String userName, String password, String userType) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return User.createUser(connection,userName, hashedPassword, userType) ? 200 : 500;
    }


    /**
     * @return int
     * 200 - success
     * 404 - user not found
     * 401 - password is incorrect
     */
    public int login(String userName, String password) {
        String[] loginInfo = User.getLoginInfo(connection,userName);
        if(loginInfo == null) return 404;
        if(!BCrypt.checkpw(password, loginInfo[0])) return 401;
        this.userName = userName;
        this.userId = loginInfo[1];
        this.userType = loginInfo[2];
        return 200;
    }


    public boolean isLoggedIn() {
        return (this.userName != null && this.userType != null && this.userId != null);
    }

    
    public boolean logout() {
        try {
            this.userName = null;
            this.userType = null;
            this.userId = null;
            connection.close();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while closing the connection");
        }
        return false;
    }

}
