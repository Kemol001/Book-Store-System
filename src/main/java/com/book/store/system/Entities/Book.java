package com.book.store.system.Entities;

import java.sql.Connection;

public class Book implements DBObj{
    private String title;
    private String author;
    private String genre;
    private double price;
    private User owner;
    private User borrower;

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
