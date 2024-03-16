package com.book.store.system.Entities;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.book.store.system.DB.DB;

public class Book implements DBObj{
    private int id;
    private String title;
    private String author;
    private String genre;
    private double price;
    private User owner;
    private User borrower;

    public boolean init(Connection cn){
        try{
            String sqlStatement = "CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT, genre TEXT, price REAL, owner INTEGER, borrower INTEGER)";
            cn.createStatement().executeQuery(sqlStatement);
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public Book(String title,String author,String genre,double price,User owner){
        try{
            String sqlStatement = "INSERT INTO books (title,author,genre,price,owner) VALUES ('"+title+"','"+author+"','"+genre+"',"+price+","+owner.getId()+")";
            Connection c = DB.getConnection();
            c.createStatement().executeQuery(sqlStatement);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void borrow(User borrower){this.borrower = borrower;}
    public void unBorrow(){this.borrower=null;}

    public String getTitle(){
        String sqlStatement = "SELECT FROM books ";
        return sqlStatement;
    }
    public String getAuthor(){return this.author;}
    public String getGenre(){return this.genre;}
    public double getPrice(){return this.price;}
    public User getOwner(){return this.owner;}
    public User getBorrower(){return this.borrower;}
}
