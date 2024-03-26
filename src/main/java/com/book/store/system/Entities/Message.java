package com.book.store.system.Entities;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class Message implements DBObj{
    public int messageId;
    public int requestId;
    public int ownerId;
    public int borrowerId;
    public String message;
    public String date;
    public String ownerName;
    public String borrowerName;
    public String senderType;
    public String bookTitle;
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

    
    synchronized public static boolean addMessage(Connection connection, int requestId,String message,String senderType){
        try {
            String sqlStatement = "INSERT INTO messages (request_id, message, senderType, date) VALUES (?, ?, ?, ?)";
            java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, requestId);
            preparedStatement.setString(2, message);
            preparedStatement.setString(3, senderType);
            preparedStatement.setString(4, new Date(System.currentTimeMillis()).toString());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while inserting the message");
        }
        return false;
    }


    synchronized public static ArrayList<Message> getMessages(Connection connection, int requestId, int messageid){
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            String sqlStatement = "SELECT m.*, r.owner_id, r.borrower_id, borrower.username AS borrower_name, owner.username AS owner_name, book.title AS book_title " +
                                 "FROM messages m " +
                                 "JOIN requests r ON m.request_id = r.id " +
                                 "JOIN books book ON r.book_id = book.id " +
                                 "JOIN users owner ON r.owner_id = owner.id " +
                                 "JOIN users borrower ON r.borrower_id = borrower.id "+
                                 "WHERE m.request_id = ? AND m.id > ? ORDER BY m.id ASC";
            java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, requestId);
            preparedStatement.setInt(2, messageid);
            java.sql.ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Message message = new Message();
                message.requestId = resultSet.getInt("request_id");
                message.message = resultSet.getString("message");
                message.messageId = resultSet.getInt("id");
                message.senderType = resultSet.getString("senderType");
                message.date = resultSet.getString("date");
                message.ownerId = resultSet.getInt("owner_id");
                message.borrowerId = resultSet.getInt("borrower_id");
                message.ownerName = resultSet.getString("owner_name");
                message.borrowerName = resultSet.getString("borrower_name");
                messages.add(message);
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while getting the messages");
        }
        return messages;
    }

}