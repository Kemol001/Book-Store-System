package com.book.store.system.Controllers;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import com.book.store.system.Entities.User;

public class UserController {
    private User user = new User();


    public int register(Connection connection,String userName, String password, String userType) {
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
    public int login(Connection connection,String userName, String password) {
        user.getLoginInfo(connection,userName);
        if(user.userName == null) return 404;
        if(!BCrypt.checkpw(password, user.password)) return 401;
        return 200;
    }


    public boolean isLoggedIn() {
        return (this.user.userId != 0);
    }

    
    public boolean logout() {
        try {
            this.user = new User();
            // connection.close();
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Error while closing the connection");
        }
        return false;
    }

}
