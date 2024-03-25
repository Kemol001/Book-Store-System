package com.book.store.system.Entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


public class Book implements DBObj{
    public String title;
    public String author;
    public String genre;
    public String ownerName;
    public String borrowerName;
    public double price;
    public int ownerId;
    public int bookId;
    public int borrowerId;
    

    public boolean init(Connection connection){
        try{
            String sqlStatement = "create table if not exists books "
            +"(id INTEGER PRIMARY KEY, "
            +"title varchar(255), "
            +"author varchar(255), "
            +"genre varchar(255), "
            +"price double, "
            +"owner_id INTEGER, "
            +"borrower_id INTEGER, "
            +"FOREIGN KEY('owner_id') REFERENCES users('id') "
            +"FOREIGN KEY('borrower_id') REFERENCES users('id'))";

            connection.createStatement().executeUpdate(sqlStatement);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error while creating the books table");
        }
        return false;
    }


    public boolean dummyBook(Connection connection){
        try {
            clearBooks(connection);
            addBook(connection, "The Great Gatsby".toLowerCase(), "F. Scott Fitzgerald".toLowerCase(), "Novel", 10, 1);
            addBook(connection, "To Kill a Mockingbird".toLowerCase(), "Harper Lee".toLowerCase(), "Novel", 15, 1);
            addBook(connection, "1984", "George Orwell".toLowerCase(), "Novel", 20, 1);
            addBook(connection, "Pride and Prejudice".toLowerCase(), "Jane Austen", "Novel", 25, 1);
            borrow(connection, 1, 3);
            borrow(connection, 2, 3);
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Error while inserting the book");
        }
        return false;
    }


    public void clearBooks(Connection connection){
        try {
            String sqlStatement = "DELETE FROM books";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while deleting the books");
        }
    }


    public static boolean addBook(Connection connection, String title,String author,String genre,double price,int ownerId){
        try {
            String sqlStatement = "INSERT INTO books (title, author, genre, price, owner_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, genre);
            preparedStatement.setDouble(4, price);
            preparedStatement.setInt(5, ownerId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while inserting the book");
        }
        return false;
    }


    public boolean borrow(Connection connection,int bookId,int borrowerId){
        try {
            String sqlStatement = "UPDATE books SET borrower_id = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, borrowerId);
            preparedStatement.setInt(2, bookId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while borrowing the book");
        }
        return false;
    }


    public boolean unBorrow(Connection connection,int bookId){
        try {
            String sqlStatement = "UPDATE books SET borrower_id = null WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, bookId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while returning the book");
        }
        return false;
    }


    public double getAttribute(Connection connection, String attribute, int bookId){
        try{
            String sqlStatement = "SELECT ? FROM books where id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, attribute);
            preparedStatement.setInt(2, bookId);
            return preparedStatement.executeQuery().getDouble("price");
        }catch(SQLException e){
            // e.printStackTrace();
            System.out.println("Error while getting the price");
        }
        return -1;
    }


    public int getUser(Connection connection, String userType,int bookId){
        try{
            String sqlStatement = "SELECT ?_id FROM books where id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, userType);
            preparedStatement.setInt(2, bookId);
            return preparedStatement.executeQuery().getInt("owner_id");
        }catch(SQLException e){
            // e.printStackTrace();
            System.out.println("Error while getting the owner");
        }
        return -1;
    }


    public static boolean delete(Connection connection,int bookId,int ownerId){
        try {
            String sqlStatement = "DELETE FROM books WHERE id = ? AND owner_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, bookId);
            preparedStatement.setInt(2, ownerId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while deleting the book");
        }
        return false;
    }


    public static ArrayList<Book> search(Connection connection, Map<String,String> attributes) {
        ArrayList<Book> books = new ArrayList<>();
        try {
            String sqlStatement = "SELECT b.*, u.username as owner_name, u2.username as borrower_name FROM books b join users u on u.id=b.owner_id left join users u2 on u2.id=b.borrower_id WHERE ";
            for (String key : attributes.keySet()) {
                if(attributes.get(key).contains("null"))
                sqlStatement += key + " is "+ attributes.get(key) +" And ";
                else if(key.equals("owner_id") || key.equals("borrower_id") || key.equals("id"))
                    sqlStatement += key + " = ? And ";
                else if(key.equals("price")){
                    sqlStatement += key + attributes.get(key);
                }
                else
                    sqlStatement += key + " like '%' || ? || '%' And ";
            }
            sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 4);
            //??????????????????????????????????????????????????????????????????????????????
            // System.out.println(sqlStatement);

            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            int i = 0;
            for (String value : attributes.values()) {
                if(value.contains("null") || value.equals("price"))
                    continue;
                preparedStatement.setString(++i, value);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.title = (resultSet.getString("title"));
                book.author = (resultSet.getString("author"));
                book.price = (resultSet.getDouble("price"));
                book.ownerId = (resultSet.getInt("owner_id"));
                book.genre = (resultSet.getString("genre"));
                book.borrowerId = (resultSet.getInt("borrower_id"));
                book.bookId = (resultSet.getInt("id"));
                book.ownerName = resultSet.getString("owner_name");
                book.borrowerName = resultSet.getString("borrower_name");
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while searching for books");
        }
        return books;
    }


    public ArrayList<Book> getAllBorrowed(Connection connection){

        ArrayList<Book> books = new ArrayList<>();

        try {
            String sqlStatement = "SELECT * FROM books WHERE borrower_id IS NOT NULL";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.title = (resultSet.getString("title"));
                book.author = (resultSet.getString("author"));
                book.price = (resultSet.getDouble("price"));
                book.ownerId = (resultSet.getInt("owner_id"));
                book.genre = (resultSet.getString("genre"));
                book.borrowerId = (resultSet.getInt("borrower_id"));
                books.add(book);
            }
            return books;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while getting all borrowed books");
        }
        return null;

    }
}