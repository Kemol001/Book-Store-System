package Entities;

public class Book {
    private String title;
    private String author;
    private String genre;
    private double price;

    public Book(String title,String author,String genre,double price){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
    }
    public String getTitle(){return this.title;}
    public String getAuthor(){return this.author;}
    public String getGenre(){return this.genre;}
    public double getPrice(){return this.price;}
}
