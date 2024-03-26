package com.book.store.system.Entities;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

public class Message implements DBObj{
    public int requestId;
    private String message;
    private String date;
    public String senderType;
    public static String dateFormat = "dd-MM-yyyy HH:mm:ss";

    @Override
    public boolean init(Connection conn) {
        try{
            String sqlStatement = "create table if not exists messages "
            +"(id INTEGER PRIMARY KEY, "
            +"request_id INTEGER, "
            +"message TEXT, "
            +"senderType TEXT,"
            +"date TEXT, "
            +"FOREIGN KEY('request_id') REFERENCES requests('id'))";
            conn.createStatement().executeUpdate(sqlStatement);
            return true;
        }catch(SQLException e){
            // e.printStackTrace();
            System.out.println("Error while creating the messages table");
        }
        return false;
    }

    public Message(){

    }
    public Message(int requestId,String message,String senderType,String date){
        this.requestId = requestId;
        this.message = message;
        this.senderType = senderType;
        this.date = date;
    }

    public static boolean addMessage(Connection connection, int requestId,String message,String senderType){
        try {
            String sqlStatement = "INSERT INTO messages (request_id, message, senderType, date) VALUES (?, ?, ?, ?)";
            java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, requestId);
            preparedStatement.setString(2, message);
            preparedStatement.setString(3, senderType);
            preparedStatement.setString(4, new Date(System.currentTimeMillis()).toString());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while inserting the message");
        }
        return false;
    }

    public int getSenderID(){return 1;}
    public String getMessageString(){return this.message;}
    public String getDate(){return this.date;}


}