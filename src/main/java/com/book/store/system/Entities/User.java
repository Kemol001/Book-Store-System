package com.book.store.system.Entities;

import java.sql.*;

public class User {
    public String name;
    public String userName;
    public String password;
    public String userType;
    public boolean isLoggedIn = false;


    public static boolean init(Connection connection){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists users (id INTEGER PRIMARY KEY, username varchar(255), password varchar(255), user_type varchar(255), is_locked boolean)");
            return true;
        }catch(Exception e){
            //e.printStackTrace();
            System.out.println("Error while creating the users table");
        }
        return false;
    }


    public static boolean createUser(Connection connection, String userName, String password, String userType){
        try{
            boolean isLocked = userType.equals("admin") ? false : true;
            Statement statement = connection.createStatement();
            return statement.executeUpdate("insert into users (username, password, user_type, is_locked) values (" + userName + ", " + password + ", " + userType + ", " + isLocked +")") > 0;
        }catch(Exception e){
            e.printStackTrace();
        }   
        return false;
    }


    public static String[] getLoginInfo(Connection connection , String userName){
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select password,user_type from users where username = " + userName);
            return new String[] {rs.getString("password"),rs.getString("user_type")};
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean UpdateLoginStatus(Connection connection, String userName, boolean status){
        try{
            Statement statement = connection.createStatement();
            return statement.executeUpdate("update users set is_locked = " + status + " where username = " + userName) > 0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public String getUserType(){return this.userType;}
}
