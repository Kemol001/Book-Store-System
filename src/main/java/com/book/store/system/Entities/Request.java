package com.book.store.system.Entities;

import java.util.Date;
import java.sql.Connection;

enum Status{
    PENDING,ACCEPTED,REJECTED
}

public class Request implements DBObj{
    private int userID;
    private int bookID;
    private Status status;
    private Date date;

    public boolean init(Connection cnx){
        return true;
    }

    public Request(int userID,int bookID){
        this.userID = userID;
        this.bookID = bookID;
        this.status = Status.PENDING;
        this.date = new Date();
    }

    public int getUserID(){return this.userID;}
    public int getBookID(){return this.bookID;}
    public Status getStatus(){return this.status;}
    public Date getDate(){return this.date;}
}