package com.book.store.system.Entities;

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

    public int getUserID(){return this.userID;}
    public int getBookID(){return this.bookID;}
    public Status getStatus(){return this.status;}
    public Date getDate(){return this.date;}
}