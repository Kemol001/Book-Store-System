package com.book.store.system.Entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;


public class Request implements DBObj{
    public int requestId;
    public int borrowerId;
    public int ownerId;
    public int bookId;
    public String borrowerName;
    public String ownerName;
    public String status;
    public String bookName;
    public Date date;


    public boolean init(Connection connection){
        try{
            String sqlStatement = "create table if not exists requests "
            +"(id INTEGER PRIMARY KEY, "
            +"borrower_id INTEGER, "
            +"owner_id INTEGER, "
            +"book_id INTEGER, "
            +"status varchar(255), "
            +"date TEXT, "
            +"FOREIGN KEY('borrower_id') REFERENCES users('id') "
            +"FOREIGN KEY('owner_id') REFERENCES users('id') "
            +"FOREIGN KEY('book_id') REFERENCES books('id'))";

            connection.createStatement().executeUpdate(sqlStatement);
            return true;
        }catch(SQLException e){
            // e.printStackTrace();
            System.out.println("Error while creating the requests table");
        }
        return false;
    }


    public Request(){
        
    }


    public boolean addRequest(Connection connection, int borrowerId, int ownerId, int bookId){
        try {
            String sqlStatement = "INSERT INTO requests (owner_id ,borrower_id , book_id, status, date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, ownerId);
            preparedStatement.setInt(2, borrowerId);
            preparedStatement.setInt(3, bookId);
            preparedStatement.setString(4, "pending");
            preparedStatement.setString(5, new Date(System.currentTimeMillis()/ 1000L).toString());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while inserting the request");
        }
        return false;
    }


    public static boolean setStatus(Connection connection, int requestId, String status){
        try {
            String sqlStatement = "UPDATE requests SET status = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, requestId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while updating the request");
        }
        return false;
    }


    public int getId(Connection connection, String idType, int requestId){
        try {
            String sqlStatement = "SELECT ?_id FROM requests WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, idType);
            preparedStatement.setInt(2, requestId);
            return preparedStatement.executeQuery().getInt(idType+"_id");
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while getting the user id");
        }
        return -1;
    }
    

    public String getStatus(Connection connection, int requestId){
        try {
            String sqlStatement = "SELECT status FROM requests WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, requestId);
            return preparedStatement.executeQuery().getString("status");
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while getting the status");
        }
        return null;
    }


    public static ArrayList<Request> getUserRequests(Connection connection, String userType ,int userId){
        ArrayList<Request> requests = new ArrayList<>();
        try {
            String sqlStatement = "SELECT r.*, b.name AS book_name, u.name AS user_name FROM requests r JOIN books b ON r.book_id = b.id JOIN users u ON r.?_id = u.id WHERE ?_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, userType.equals("owner")?"borrower":"owner");
            preparedStatement.setString(2, userType);
            preparedStatement.setInt(3, userId);
            var resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Request request = new Request();
                request.requestId = resultSet.getInt("id");
                request.bookId = resultSet.getInt("book_id");
                request.status = resultSet.getString("status");
                request.ownerId = resultSet.getInt("owner_id");
                request.borrowerId = resultSet.getInt("borrower_id");
                request.date = new Date(resultSet.getLong("date"));
                if(userType.equals("owner"))
                    request.borrowerName = resultSet.getString("user_name");
                else
                    request.ownerName = resultSet.getString("user_name");
                request.bookName = resultSet.getString("book_name");
                requests.add(request);
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while getting the requests");
        }
        return requests;
    }

    
    public ArrayList<Request> getAllRequests(Connection connection, String status){
        ArrayList<Request> requests = new ArrayList<>();
        try {
            if(status.equals("all"))
                status = ".*";
            String sqlStatement = "SELECT * FROM requests where status like ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, status);
            var resultSet = preparedStatement.executeQuery(sqlStatement);
            while(resultSet.next()){
                Request request = new Request();
                request.requestId = resultSet.getInt("id");
                request.bookId = resultSet.getInt("book_id");
                request.status = resultSet.getString("status");
                request.ownerId = resultSet.getInt("owner_id");
                request.borrowerId = resultSet.getInt("borrower_id");
                request.date = new Date(resultSet.getLong("date"));
                requests.add(request);
            }
            return requests;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while getting the requests");
        }
        return null;
    }
    
}