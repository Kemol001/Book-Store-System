package com.book.store.system.Entities;

public class User {
    private String name;
    private String userName;
    private String password;
    private String userType;

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
}
