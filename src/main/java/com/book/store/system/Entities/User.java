package com.book.store.system.Entities;

import java.sql.*;

public class User implements DBObj{
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


    public static User getLoginInfo(Connection connection , String userName){
        try{
            String sqlStatement = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                User user = new User();
                user.userName = resultSet.getString("username");
                user.password = resultSet.getString("password");
                user.userType = resultSet.getString("user_type");
                return user;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
