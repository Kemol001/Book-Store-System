package com.book.store.system.Entities;

import java.sql.Connection;

public class Book {
    private String title;
    private String author;
    private String genre;
    private double price;
    private User owner;
    private User borrower;

    public Book(String title,String author,String genre,double price,User owner){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.owner = owner;
        this.borrower = null;
    }

    public static boolean init(Connection connection){
        return true;
        // try{
        //     Statement statement = connection.createStatement();
        //     statement.executeUpdate("create table if not exists requests (id int primary key auto_increment, user_id int, book_id int, status varchar(255), date date)");
        //     return true;
        // }catch(Exception e){
        //     e.printStackTrace();
        // }
    }


    public void borrow(User borrower){this.borrower = borrower;}
    public void unBorrow(){this.borrower=null;}

    public String getTitle(){return this.title;}
    public String getAuthor(){return this.author;}
    public String getGenre(){return this.genre;}
    public double getPrice(){return this.price;}
    public User getOwner(){return this.owner;}
    public User getBorrower(){return this.borrower;}
}
