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
    public String bookTitle;
    public String date;


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


    public static boolean addRequest(Connection connection, int borrowerId, int ownerId, int bookId){
        try {
            String sqlStatement = "INSERT INTO requests (owner_id ,borrower_id , book_id, status, date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, ownerId);
            preparedStatement.setInt(2, borrowerId);
            preparedStatement.setInt(3, bookId);
            preparedStatement.setString(4, "pending");
            preparedStatement.setString(5, new Date(System.currentTimeMillis()).toString());
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


    public static ArrayList<Request> getUserRequests(Connection connection, String userType ,int userId){
        ArrayList<Request> requests = new ArrayList<>();
        try {
            String sqlStatement = "SELECT r.*, b.title AS book_title, u.username AS user_name FROM requests r JOIN books b ON r.book_id = b.id JOIN users u ON r."+(userType.equals("owner")?"borrower":"owner")+"_id = u.id WHERE r."+userType+"_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, userId);
            var resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Request request = new Request();
                request.requestId = resultSet.getInt("id");
                request.bookId = resultSet.getInt("book_id");
                request.status = resultSet.getString("status");
                request.ownerId = resultSet.getInt("owner_id");
                request.borrowerId = resultSet.getInt("borrower_id");
                request.date = resultSet.getString("date");
                if(userType.equals("owner"))
                    request.borrowerName = resultSet.getString("user_name");
                else
                    request.ownerName = resultSet.getString("user_name");
                request.bookTitle = resultSet.getString("book_title");
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while getting the requests");
        }
        return requests;
    }

    
    public static ArrayList<Request> getAllRequests(Connection connection, String status){
        ArrayList<Request> requests = new ArrayList<>();
        try {
            if(status.equals("all"))
                status = ".*";
            String sqlStatement = "SELECT r.*, b.title AS book_title, u.username AS borrower_name, u2.username AS owner_name FROM requests r JOIN books b ON r.book_id = b.id JOIN users u ON r.borrower_id = u.id JOIN users u2 ON r.owner_id = u2.id WHERE r.status LIKE '%' || ? || '%'";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, status);
            var resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Request request = new Request();
                request.requestId = resultSet.getInt("id");
                request.bookId = resultSet.getInt("book_id");
                request.status = resultSet.getString("status");
                request.ownerId = resultSet.getInt("owner_id");
                request.borrowerId = resultSet.getInt("borrower_id");
                request.borrowerName = resultSet.getString("borrower_name");
                request.ownerName = resultSet.getString("owner_name");
                request.bookTitle = resultSet.getString("book_title");
                request.date = resultSet.getString("date");
                requests.add(request);
            }
            return requests;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while getting the requests");
        }
        return null;
    }


    public static Request getRequest(Connection connection, int requestId){
        try {
            String sqlStatement = "SELECT r.*, b.title AS book_title, u.username AS borrower_name, u2.username AS owner_name FROM requests r JOIN books b ON r.book_id = b.id JOIN users u ON r.borrower_id = u.id JOIN users u2 ON r.owner_id = u2.id WHERE r.id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, requestId);
            var resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Request request = new Request();
                request.requestId = resultSet.getInt("id");
                request.bookId = resultSet.getInt("book_id");
                request.status = resultSet.getString("status");
                request.ownerId = resultSet.getInt("owner_id");
                request.borrowerId = resultSet.getInt("borrower_id");
                request.borrowerName = resultSet.getString("borrower_name");
                request.ownerName = resultSet.getString("owner_name");
                request.bookTitle = resultSet.getString("book_title");
                request.date = resultSet.getString("date");
                return request;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while getting the request");
        }
        return null;
    }


    public static ArrayList<Request> getAcceptedRequests(Connection connection, int userId){
        ArrayList<Request> requests = new ArrayList<>();
        try {
            String sqlStatement = "SELECT r.*, b.title AS book_title, u.username AS borrower_name, u2.username AS owner_name FROM requests r JOIN books b ON r.book_id = b.id JOIN users u ON r.borrower_id = u.id JOIN users u2 ON r.owner_id = u2.id WHERE r.status = 'accept' AND (r.borrower_id = ? OR r.owner_id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            var resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Request request = new Request();
                request.requestId = resultSet.getInt("id");
                request.bookId = resultSet.getInt("book_id");
                request.status = resultSet.getString("status");
                request.ownerId = resultSet.getInt("owner_id");
                request.borrowerId = resultSet.getInt("borrower_id");
                request.borrowerName = resultSet.getString("borrower_name");
                request.ownerName = resultSet.getString("owner_name");
                request.bookTitle = resultSet.getString("book_title");
                request.date = resultSet.getString("date");
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while getting the requests");
        }
        return requests;
    }


}