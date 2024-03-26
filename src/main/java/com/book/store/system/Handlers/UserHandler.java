package com.book.store.system.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.book.store.system.Constants;
import com.book.store.system.Controllers.MessageController;
import com.book.store.system.Entities.*;

public class UserHandler implements Handler{
    private ClientHandler clientHandler;

    public UserHandler(ClientHandler clientHandler){
        this.clientHandler = clientHandler;
    }

    public void chat(String userType,int requestID){
        MessageHandler messageHandler = MessageController.getHandler(requestID);
        if(Objects.isNull(messageHandler)){
            messageHandler = new MessageHandler(clientHandler,userType,requestID);
            MessageController.addHandler(messageHandler);
        }
        try{
            messageHandler.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void removeBook() throws Exception{
        Map<String, String> arguments = new HashMap<>();
        arguments.put("owner_id",String.valueOf(clientHandler.userController.getUser().userId));
        arguments.put("borrower_id","null");
        ArrayList<Book> myBooks = Book.search(clientHandler.connection,arguments);
        clientHandler.sendMessage("Choose The ID Of The Book You Wish To Remove or "+Constants.EXIT+" to exit.\n"+Constants.SPACER,false);
        for(Book book : myBooks){
            clientHandler.sendMessage(book.bookId+")Title: "+book.title+" Author: "+book.author+" Genre: "+book.genre+
            " Price: "+book.price,false);
        }
        clientHandler.sendMessage("");
        String choice = clientHandler.reader.readLine();
        if(choice.equals(Constants.EXIT)) return;
        else if(Book.delete(clientHandler.connection,Integer.parseInt(choice),clientHandler.userController.getUser().userId))
            clientHandler.sendMessage("Book Removed Succesfully!\n"+Constants.SPACER,false);
        else
            clientHandler.sendMessage("Error While Removing Book\n"+Constants.SPACER,false);
    }


    public void addBook() throws Exception{
        String title,author,genre;
        double price;
        clientHandler.sendMessage("Enter Book Title:"); title = clientHandler.reader.readLine().toLowerCase();
        clientHandler.sendMessage("Enter Book Author:"); author = clientHandler.reader.readLine().toLowerCase();
        clientHandler.sendMessage("Enter Book Genre (comma separated):"); genre = clientHandler.reader.readLine().toLowerCase();
        clientHandler.sendMessage("Enter Book Price:"); price = Double.parseDouble(clientHandler.reader.readLine());
        if(Book.addBook(clientHandler.connection, title,author,genre,price,clientHandler.userController.getUser().userId))
            clientHandler.sendMessage("Book Added Succesfully!\n"+Constants.SPACER,false);
        else
            clientHandler.sendMessage("Error While Adding Book\n"+Constants.SPACER,false);
    }


    public void searchBooks() throws Exception{
        clientHandler.sendMessage("Enter The Category You Want To Search By And The Value (Space Seprated)\n"+
            "Available Categories (Title/Author/Genre/Price)\nAnd "+ Constants.EXIT +" to stop taking input\n"+ Constants.SPACER);
        String line;
        Map<String, String> attributes = new HashMap<>();
        while (!(line = clientHandler.reader.readLine().toLowerCase()).equals(Constants.EXIT)){
            String[] parts = line.split("\\s+", 2); // Split line into two parts (category and value)
            if (parts.length == 2) {
                String category = parts[0].trim().toLowerCase();
                String value = parts[1].trim().toLowerCase();
                attributes.put(category, value); // Add category and value to the map
            } else {
                // Handle invalid input
                clientHandler.sendMessage("Invalid input format. Please enter category and value separated by space.\n"+ Constants.SPACER,false);
            }
            clientHandler.sendMessage("");
        }
        ArrayList<Book> books = Book.search(clientHandler.connection,attributes);
        int i = 1;
        for(Book book : books){
            clientHandler.sendMessage("id: "+(i++)+" Title: "+book.title+" Author: "+book.author+" Genre: "+book.genre+
            " Price: "+book.price+" Book Owner: "+book.ownerName,false);
        }
        clientHandler.sendMessage(Constants.SPACER+"\nChoose The ID Of The Book You Want To Request or exit");
        while(!(line = clientHandler.reader.readLine().toLowerCase()).equals(Constants.EXIT)){
            int bookId = Integer.parseInt(line)-1;
            if(bookId < 0 || bookId > books.size()){
                clientHandler.sendMessage("Invalid Book ID\n"+Constants.SPACER);
                continue;
            }
            Book book = books.get(bookId);
            if(Request.addRequest(clientHandler.connection,clientHandler.userController.getUser().userId,book.ownerId,book.bookId)){
                clientHandler.sendMessage("Request Sent Succesfully!\n"+Constants.SPACER);
            }else{
                clientHandler.sendMessage("Error While Sending Request\n"+Constants.SPACER);
            }
        }
        
    }
    

    public void reviewRequests() throws Exception{
        ArrayList<Request> myRequests = Request.getUserRequests(clientHandler.connection,"owner",clientHandler.userController.getUser().userId);
        clientHandler.sendMessage(clientHandler.userController.getUser().userName+" Recieved Requests:\n"+Constants.SPACER,false);
        for(Request request : myRequests){
            clientHandler.sendMessage(request.requestId+")Book Name: "+request.bookTitle+" Borrower Name: "+
            request.borrowerName+"\n"+ Constants.SPACER,false);
        }
        clientHandler.sendMessage("Type Request ID (Accept/Reject) Or Exit constants\n"+Constants.SPACER);
        String input = clientHandler.reader.readLine();
        if(!(input.equals(Constants.EXIT))){
            String parts [] = input.split("\\s+",2);
            if(parts.length == 2){
                int requestID = Integer.parseInt(parts[0].trim());
                String status = (parts[1].trim());
                if(status == "Accept" && Request.setStatus(clientHandler.connection,requestID,status)){
                    clientHandler.sendMessage("Request Accepted Succesfully!\n"+Constants.SPACER,false);
                    //TODO :Implement messaging logic
                }
                else if(status == "Reject" && Request.setStatus(clientHandler.connection,requestID,status)){
                    clientHandler.sendMessage("Request Rejected Succesfully!\n"+Constants.SPACER,false);
                }
                else{
                    clientHandler.sendMessage("Error While Processing Request\n"+Constants.SPACER,false);
                }
            }
            else{
                clientHandler.sendMessage("Invalid Input Format\n"+Constants.SPACER,false); 
            }
        }
    }
    

    public void getRequestsHistory() throws Exception{
        ArrayList<Request> myRequests = Request.getUserRequests(clientHandler.connection,"borrower",clientHandler.userController.getUser().userId);
        clientHandler.sendMessage(clientHandler.userController.getUser().userName+" Submitted Requests:\n"+Constants.SPACER,false);
        for(Request request : myRequests){
            clientHandler.sendMessage("request id: "+request.requestId+" Book Name: "+request.bookTitle+" Owner Name: "+
            request.ownerName+" Status: "+request.status+" request date: "+request.date.toString(),false);
        }
        clientHandler.sendMessage(Constants.SPACER,false);
    }


    public void start() throws Exception{
        clientHandler.sendMessage("Choose an Action\n" + Constants.SPACER, false);
        while (true) {
            clientHandler.sendMessage("1- Browse And Search Books \n2- Add Or Remove Books\n" +
            "3- Review Requests\n4- View Request History\n5- Logout\n"+ Constants.SPACER);
            String inputLine = clientHandler.reader.readLine();
            if (inputLine.equals("1")) {
                searchBooks();
            }
            else if(inputLine.equals("2")){
                clientHandler.sendMessage("1- Add a book\n2- Remove A Book\n"+ Constants.SPACER);
                String choice = clientHandler.reader.readLine();
                if(choice.equals("1")){
                    addBook();
                }
                else if(choice.equals("2")){
                    removeBook();
                } 
            }
            else if(inputLine.equals("3")){
                reviewRequests();
            }
            else if(inputLine.equals("4")){
                getRequestsHistory();
            }
            else if(inputLine.equals("5")){
                clientHandler.sendMessage("See You soon!\n"+Constants.SPACER,false);
                clientHandler.userController.logout();
                break;
            }else{
                clientHandler.sendMessage("Invalid Input!!!\n"+Constants.SPACER,false);
            }
        }
    }
}
