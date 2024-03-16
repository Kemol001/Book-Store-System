package com.book.store.system.Entities;

import java.sql.Connection;

public class User implements DBObj{
    private int id;
    private String name;
    private String userName;
    private String password;
    private String userType;

    public boolean init(Connection cnx){
        String query = "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), userName VARCHAR(100), password VARCHAR(100), userType VARCHAR(100))";
        try {
            cnx.createStatement().execute(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User(String name,String userName,String password,String userType){
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.userType = userType;
    }
    
    public String getName(){return this.name;}
    public String getUserName(){return this.userName;}
    public String getPassword(){return this.password;}
    public String getUserType(){return this.userType;}
    public int getId(){return this.id;}
}
