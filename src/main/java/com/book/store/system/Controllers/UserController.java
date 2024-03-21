package com.book.store.system.Controllers;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

import com.book.store.system.Db.Db;
import com.book.store.system.Entities.User;

public class UserController {
    private String userName;
    private String userType;
    private Connection connection;

    public UserController() {
        this.connection = Db.connect();
    }

    public boolean register(String userName, String password, String userType) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return User.createUser(connection,userName, hashedPassword, userType);
    }


    /**
     * @return int
     * 0 - success
     * 1 - user not found
     * 2 - password is incorrect
     * 3 - error in updating login status
     */
    public int login(String userName, String password) {
        String[] loginInfo = User.getLoginInfo(connection,userName);
        if(loginInfo == null) return 1;
        if(!BCrypt.checkpw(password, loginInfo[0])) return 2;
        this.userName = userName;
        this.userType = loginInfo[1];
        if(!User.UpdateLoginStatus(connection, userName, true)) return 3;
        return 0;
    }

    
    public boolean logout() {
        try {
            this.userName = null;
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
