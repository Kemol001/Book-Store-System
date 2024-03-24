package com.book.store.system.Handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.book.store.system.Constants;
import com.book.store.system.Controllers.UserController;
import com.book.store.system.Database.Db;
import com.book.store.system.Entities.Book;
import com.book.store.system.Entities.Request;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private Connection connection;
    private UserController userController = new UserController();

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.connection = Db.connect();
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendMessage(String message, boolean isEOM) {
        try {
            String eom = isEOM ? Constants.EOM + "\n" : "";
            String tempMessage = (message.length()==0 ? "" : message + "\n");
            writer.write(tempMessage + eom);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, true);
    }

    public void startPage() throws Exception {
        sendMessage("Welcome to the Book Store!\n" + Constants.SPACER, false);
        while (true) {
            sendMessage("1- Register\n2- Login\n3- Exit\n" + Constants.SPACER); String inputLine = reader.readLine();
            if (inputLine.equals("3")) {
                sendMessage("Goodbye!\n" + Constants.SPACER +"\n" + Constants.EXIT);
                break;
            }
            sendMessage("Enter your username: "); String userName = reader.readLine();
            sendMessage("Enter your password:"); String password = reader.readLine();
            if (inputLine.equals("1")) {
                sendMessage("Enter Role (admin/user): ");
                String role = reader.readLine();
                if (!role.equals("admin") && !role.equals("user")) {
                    sendMessage("Invalid role!!!\n" + Constants.SPACER, false);
                    continue;
                }
                if (userController.register(connection,userName, password, role) != Constants.ERROR) {
                    sendMessage("User registered successfully!\n" + Constants.SPACER, false);
                } else {
                    sendMessage("Error " + Constants.ERROR + " while registering the user!\n" + Constants.SPACER,
                            false);
                }
            } else if (inputLine.equals("2")) {
                int result = userController.login(connection,userName, password);
                if (result == Constants.SUCCESS) {
                    sendMessage(Constants.SPACER+"\nWelcome " + userName + "!\n" + Constants.SPACER,false);
                    break;
                } else if (result == Constants.USER_NOT_FOUND) {
                    sendMessage("Error " + Constants.USER_NOT_FOUND + " User not found!!!\n" + Constants.SPACER, false);
                } else if (result == Constants.WRONG_PASSWORD) {
                    sendMessage("Error " + Constants.WRONG_PASSWORD + " Wrong password!!!\n" + Constants.SPACER, false);
                } else {
                    sendMessage("Error " + Constants.ERROR + " while logging in!\n" + Constants.SPACER, false);
                }

            } else {
                sendMessage("Invalid input!!!\n" + Constants.SPACER);
            }
        }

    }

    public void userPage() throws Exception{
        sendMessage("Choose an Action\n" + Constants.SPACER, false);
        while (true) {
            sendMessage("1- Browse And Search Books \n2- Add Or Remove Books\n" +
            "3- Review Requests\n4- View Request History\n5- Logout\n"+ Constants.SPACER);
            String inputLine = reader.readLine();
            if (inputLine.equals("1")) {
                sendMessage("Enter The Category You Want To Search By And The Value (Space Seprated)\n"+
                "Available Categories (Title/Author/Genre/Price)\nAnd "+ Constants.EXIT +" to stop taking input\n"+ Constants.SPACER);
                String line;
                Map<String, String> attributes = new HashMap<>();
                while (!(line = reader.readLine().toLowerCase()).equals(Constants.EXIT)){
                    String[] parts = line.split("\\s+", 2); // Split line into two parts (category and value)
                    if (parts.length == 2) {
                        String category = parts[0].trim().toLowerCase();
                        String value = parts[1].trim().toLowerCase();
                        attributes.put(category, value); // Add category and value to the map
                    } else {
                        // Handle invalid input
                        sendMessage("Invalid input format. Please enter category and value separated by space.\n"+ Constants.SPACER);
                    }
                    sendMessage("");
                }
                ArrayList<Book> books = Book.search(connection,attributes);
                for(Book book : books){
                    sendMessage("id: "+book.bookId+" Title: "+book.title+" Author: "+book.author+" Genre: "+book.genre+
                    " Price: "+book.price+" Book Owner: "+book.ownerName,false);
                }
                // sendMessage(Constants.SPACER+"\nChoose The ID Of The Book You Want To Request or exit");
                // while(!(line = reader.readLine().toLowerCase()).equals(Constants.EXIT)){
                //     int bookId = Integer.parseInt(line);
                //     if(Request.addRequest(connection,bookId,userController.getUser().userid)){
                //         sendMessage("Request Sent Succesfully!\n"+Constants.SPACER,false);
                //     }else{
                //         sendMessage("Error While Sending Request\n"+Constants.SPACER,false);
                //     }
                // }
                
            }
            else if(inputLine.equals("2")){
                sendMessage("1- Add a book\n2- Remove A Book\n"+ Constants.SPACER);
                String choice = reader.readLine();
                if(choice.equals("1")){
                    String title,author,genre;
                    double price;
                    sendMessage("Enter Book Title\n"+Constants.SPACER); title = reader.readLine().toLowerCase();
                    sendMessage("Enter Book Author\n"+Constants.SPACER); author = reader.readLine().toLowerCase();
                    sendMessage("Enter Book Genre (comma separated)\n"+Constants.SPACER); genre = reader.readLine().toLowerCase();
                    sendMessage("Enter Book Price\n"+Constants.SPACER); price = Double.parseDouble(reader.readLine());
                    if(Book.addBook(connection, title,author,genre,price,userController.getUser().userId))
                        sendMessage("Book Added Succesfully!\n"+Constants.SPACER,false);
                    else
                        sendMessage("Error While Adding Book\n"+Constants.SPACER,false);
                }
                else if(choice.equals("2")){
                    Map<String, String> user = new HashMap<>();
                    user.put("owner_id",String.valueOf(userController.getUser().userId));
                    user.put("borrower_id","null");
                    ArrayList<Book> myBooks = Book.search(connection,user);
                    sendMessage("Choose The ID Of The Book You Wish To Remove\n"+Constants.SPACER,false);
                    for(Book book : myBooks){
                        sendMessage(book.bookId+")Title: "+book.title+" Author: "+book.author+" Genre: "+book.genre+
                        " Price: "+book.price+"\n"+ Constants.SPACER);
                    }
                    String choice2 = reader.readLine();
                    if(Book.delete(connection,Integer.parseInt(choice2),userController.getUser().userId))
                        sendMessage("Book Removed Succesfully!\n"+Constants.SPACER,false);
                    else
                        sendMessage("Error While Removing Book\n"+Constants.SPACER,false);
                } 
            }
            else if(inputLine.equals("3")){
                ArrayList<Request> myRequests = Request.getUserRequests(connection,"owner",userController.getUser().userId);
                sendMessage(userController.getUser().userName+" Recieved Requests:\n"+Constants.SPACER,false);
                for(Request request : myRequests){
                    sendMessage(request.requestId+")Book Name: "+request.bookName+" Borrower Name: "+
                    request.borrowerName+"\n"+ Constants.SPACER,false);
                }
                sendMessage("Type Request ID (Accept/Reject) Or Exit constants\n"+Constants.SPACER);
                String input = reader.readLine();
                if(!(input.equals(Constants.EXIT))){
                    String parts [] = input.split("\\s+",2);
                    if(parts.length == 2){
                        int requestID = Integer.parseInt(parts[0].trim());
                        String status = (parts[1].trim());
                        if(status == "Accept" && Request.setStatus(connection,requestID,status)){
                            sendMessage("Request Accepted Succesfully!\n"+Constants.SPACER,false);
                            //TODO :Implement messaging logic
                        }
                        else if(status == "Reject" && Request.setStatus(connection,requestID,status)){
                            sendMessage("Request Rejected Succesfully!\n"+Constants.SPACER,false);
                        }
                        else{
                            sendMessage("Error While Processing Request\n"+Constants.SPACER,false);
                        }
                    }
                    else{
                       sendMessage("Invalid Input Format\n"+Constants.SPACER,false); 
                    }
                }
            }
            else if(inputLine.equals("4")){
                ArrayList<Request> myRequests = Request.getUserRequests(connection,"borrower",userController.getUser().userId);
                sendMessage(userController.getUser().userName+" Submitted Requests:\n"+Constants.SPACER,false);
                for(Request request : myRequests){
                    sendMessage(request.requestId+")Book Name: "+request.bookName+" Owner Name: "+
                    request.borrowerName+"\n"+ Constants.SPACER,false);
                }
            }
            else if(inputLine.equals("5")){
                sendMessage("See You soon!\n"+Constants.SPACER,false);
                userController.logout(connection);
                break;
            }else{
                sendMessage("Invalid Input!!!\n"+Constants.SPACER);
            }
        }
    }
    @Override
    public void run() {
        try {

            while (true) {
                startPage();

                if (!userController.isLoggedIn()) {
                    break;
                }

                if(userController.getUser().userType.equals("admin")){
                    //Admin Page
                }else{
                    userPage();
                }

            }

            clientSocket.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
