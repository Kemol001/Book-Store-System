package com.book.store.system.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.book.store.system.Constants;
import com.book.store.system.Entities.Book;
import com.book.store.system.Entities.Request;

public class AdminHandler implements Handler{
    
    private ClientHandler clientHandler;

    public AdminHandler(ClientHandler clientHandler){
        this.clientHandler = clientHandler;
    }

    
    public void getAvailableBooks() throws Exception{
        Map<String, String> arguments = new HashMap<>();
        arguments.put("title","");
        ArrayList<Book> books = Book.search(clientHandler.connection,arguments);
        clientHandler.sendMessage("Available Books:\n"+Constants.SPACER,false);
        for(Book book : books){
            clientHandler.sendMessage("Book ID: "+book.bookId+" Title: "+book.title+" Author: "+book.author+
            " Genre: "+book.genre+" Price: "+book.price+" Owner Name: "+book.ownerName,false);
        }
    }


    public void getBorrowedBooks() throws Exception{
        Map<String, String> arguments = new HashMap<>();
        arguments.put("borrower_id","not null");
        ArrayList<Book> books = Book.search(clientHandler.connection,arguments);
        clientHandler.sendMessage("Borrowed Books:\n"+Constants.SPACER,false);
        for(Book book : books){
            clientHandler.sendMessage("Book ID: "+book.bookId+" Title: "+book.title+" Author: "+book.author+
            " Genre: "+book.genre+" Price: "+book.price+" Owner Name: "+book.ownerName+" Borrower Name: "+book.borrowerName,false);
        }
    }


    public void getRequests() throws Exception{
        ArrayList<Request> requests = Request.getAllRequests(clientHandler.connection,"");
        clientHandler.sendMessage("All Requests:\n"+Constants.SPACER,false);
        for(Request request : requests){
            clientHandler.sendMessage("Request ID: "+request.requestId+" Book Name: "+request.bookTitle+
            " Owner Name: "+request.ownerName+" Borrower Name: "+request.borrowerName+
            " Status: "+request.status+" Date: "+request.date,false);
        }
        clientHandler.sendMessage(Constants.SPACER,false);
    }


    public void start() throws Exception{
        clientHandler.sendMessage("Choose an Action\n" + Constants.SPACER, false);
        while(true){
            clientHandler.sendMessage("1- books statistics\n2- Requests statistics\n3- logout\n"+ Constants.SPACER);
            String inputLine = clientHandler.reader.readLine();
            if(inputLine.equals("1")){
                clientHandler.sendMessage("1- Available Books\n2- Borrowed Books\n3- Exit\n"+ Constants.SPACER);
                String choice = clientHandler.reader.readLine();

                if(choice.equals("1")){
                    getAvailableBooks();
                }
                else if(choice.equals("2")){
                    getBorrowedBooks();
                }
                else if(choice.equals("3")){
                    break;
                }
                else{
                    clientHandler.sendMessage("Invalid Input!!!",false);
                }
                clientHandler.sendMessage(Constants.SPACER,false);

            }
            else if(inputLine.equals("2")){
                getRequests();
            }
            else if(inputLine.equals("3")){
                clientHandler.sendMessage("See You soon!\n"+Constants.SPACER,false);
                clientHandler.userController.logout();
                break;
            }
            else{
                clientHandler.sendMessage("Invalid Input!!!\n"+Constants.SPACER,false);
            }
        }
    }
}
