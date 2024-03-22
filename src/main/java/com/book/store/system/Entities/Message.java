package com.book.store.system.Entities;

import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class

public class Message{
    private int senderId;
    private String message;
    private LocalDateTime date;
    public static String dateFormat = "dd-MM-yyyy HH:mm:ss";

    public Message(int senderId,String message){
        this.senderId = senderId;
        this.message = message;
        this.date = LocalDateTime.now();
    }

    public int getSenderID(){return this.senderId;}
    public String getMessageString(){return this.message;}
    public LocalDateTime getDate(){return this.date;}

}