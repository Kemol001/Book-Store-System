package com.book.store.system.Entities;

import java.sql.Connection;

public class Book implements DBObj{
    private String title;
    private String author;
    private String genre;
    private double price;
    private User owner;
    private User borrower;

    public boolean init(Connection cn){
        try{
            // String sqlStatement = "CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT, genre TEXT, price REAL, owner INTEGER, borrower INTEGER)";
            // cn.createStatement().executeQuery(sqlStatement);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Book(){
        
    }

    public Book(String title,String author,String genre,double price,User owner){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.owner = owner;
        this.borrower = null;
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
