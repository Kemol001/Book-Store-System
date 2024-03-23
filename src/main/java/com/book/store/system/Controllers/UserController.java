package com.book.store.system.Controllers;

import org.mindrot.jbcrypt.BCrypt;
import org.sqlite.ExtendedCommand.SQLExtension;

import java.sql.*;

import com.book.store.system.Db.Db;
import com.book.store.system.Entities.User;

public class UserController {
    private User user;
    private Connection connection;

    public UserController() {
        this.connection = Db.connect();
    }

    public int register(String userName, String password, String userType) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return User.createUser(connection,userName, hashedPassword, userType) ? 200 : 500;
    }

    public User getUser() {
        return user;
    }   

    /**
     * @return int
     * 200 - success
     * 404 - user not found
     * 401 - password is incorrect
     */
    public int login(String userName, String password) {
        User loginInfo = User.getLoginInfo(connection,userName);
        if(loginInfo == null) return 404;
        if(!BCrypt.checkpw(password, loginInfo.password)) return 401;
        this.user = loginInfo;
        return 200;
    }


    public boolean isLoggedIn() {
        return (this.user != null);
    }

    
    public boolean logout() {
        try {
            this.user = null;
            connection.close();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while closing the connection");
        }
        return false;
    }

}
