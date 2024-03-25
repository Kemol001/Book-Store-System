package com.book.store.system.Entities;

import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class User implements DBObj{
    public int userId;
    public String userName;
    public String password;
    public String userType;


    public boolean init(Connection connection){
        try{
            String sqlStatement = "create table if not exists users "
            +"(id INTEGER PRIMARY KEY, "
            +"username varchar(255) UNIQUE, "
            +"password varchar(255), "
            +"user_type varchar(255))";

            connection.createStatement().executeUpdate(sqlStatement);
            return true;
        }catch(Exception e){
            //e.printStackTrace();
            System.out.println("Error while creating the users table");
        }
        return false;
    }


    public boolean dummyUser(Connection connection){
        clearUsers(connection);
        String hashedPassword = BCrypt.hashpw("test", BCrypt.gensalt());
        String hashedPassword2 = BCrypt.hashpw("admin", BCrypt.gensalt());
        String hashedPassword3 = BCrypt.hashpw("test1", BCrypt.gensalt());
        createUser(connection, "test", hashedPassword, "user");
        createUser(connection, "admin", hashedPassword2, "admin");
        createUser(connection, "test1", hashedPassword3, "user");
        return true;
    }


    public void clearUsers(Connection connection){
        try{
            String sqlStatement = "DELETE FROM users";
            connection.createStatement().executeUpdate(sqlStatement);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static boolean createUser(Connection connection, String userName, String password, String userType){
        try{
            String sqlStatement = "INSERT INTO users (username, password, user_type) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, userType);
            return preparedStatement.executeUpdate() > 0;
        }catch(Exception e){
            e.printStackTrace();
        }   
        return false;
    }


    public boolean getLoginInfo(Connection connection , String userName){
        try{
            String sqlStatement = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                this.userId =  resultSet.getInt("id");
                this.userName = resultSet.getString("username");
                this.password = resultSet.getString("password");
                this.userType = resultSet.getString("user_type");
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
