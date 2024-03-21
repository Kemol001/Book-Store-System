package com.book.store.system.Entities;

import java.sql.Connection;
import java.util.Date;

enum Status{
    PENDING,ACCEPTED,REJECTED
}

public class Request{
    private int userID;
    private int bookID;
    private Status status;
    private Date date;

    public Request(int userID,int bookID){
        this.userID = userID;
        this.bookID = bookID;
        this.status = Status.PENDING;
        this.date = new Date();
    }

    public static boolean init(Connection connection){
        return true;
        // try{
        //     Statement statement = connection.createStatement();
        //     statement.executeUpdate("create table if not exists requests (id int primary key auto_increment, user_id int, book_id int, status varchar(255), date date)");
        //     return true;
        // }catch(Exception e){
        //     e.printStackTrace();
        // }
    }

    public int getUserID(){return this.userID;}
    public int getBookID(){return this.bookID;}
    public Status getStatus(){return this.status;}
    public Date getDate(){return this.date;}
}