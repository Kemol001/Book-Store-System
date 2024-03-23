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

import com.book.store.system.Db.Db;
import com.book.store.system.Constants;
import com.book.store.system.Controllers.UserController;
import com.book.store.system.Entities.Book;

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

            writer.write(message + "\n" + eom);
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
            sendMessage("1- Register\n2- Login\n3- Exit\n" + Constants.SPACER);
            String inputLine = reader.readLine();
            if (inputLine.equals("3")) {
                sendMessage("Goodbye!\n" + Constants.SPACER);
                break;
            }
            sendMessage("Enter your username: ");
            String userName = reader.readLine();
            sendMessage("Enter your password:");
            String password = reader.readLine();
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
                    sendMessage("Welcome " + userName + "!\n" + Constants.SPACER);
                    break;
                } else if (result == Constants.USER_NOT_FOUND) {
                    sendMessage("Error " + Constants.USER_NOT_FOUND + "User not found!!!\n" + Constants.SPACER, false);
                } else if (result == Constants.WRONG_PASSWORD) {
                    sendMessage("Error " + Constants.WRONG_PASSWORD + "Wrong password!!!\n" + Constants.SPACER, false);
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
            sendMessage("1-Browse And Search Books \n2- Add Or Remove Books\n" +
            "3- Review Requests\n4- View Request History\n5- Logout\n"+ Constants.SPACER);
            String inputLine = reader.readLine();
            if (inputLine.equals("1")) {
                /*
                 * title
                 * author
                 * genre
                 * price
                 */
                sendMessage("Enter The Category You Want To Search By And The Value(Space Seprated)\n"+
                "Available Categories(Title/Author/Genre/Price\n)"+ Constants.SPACER);
                String line;
                Map<String, String> attributes = new HashMap<>();
                while ((line = reader.readLine()).equals(Constants.EOM)){
                    String[] parts = line.split("\\s+", 2); // Split line into two parts (category and value)
                    if (parts.length == 2) {
                        String category = parts[0].trim();
                        String value = parts[1].trim();
                        attributes.put(category, value); // Add category and value to the map
                    } else {
                        // Handle invalid input
                        sendMessage("Invalid input format. Please enter category and value separated by space.\n"+ Constants.SPACER);
                    }
                    
                }
                ArrayList<Book> books = new ArrayList<>();
                books = Book.search(connection,attributes);
                for(Book book : books){
                    sendMessage("Title: "+book.title+" Author: "+book.author+" Genre: "+book.genre+
                    " Price: "+book.price+" Book Owner: "+book.ownerId+"\n"+ Constants.SPACER);
                }
            }
            else if(inputLine.equals("2")){
                sendMessage("Do You Want To 1)Add Or 2)Remove A Book?"+ Constants.SPACER);
                String choice = reader.readLine();
                if(choice.equals("1")){
                    String title,author,genre;
                    double price;
                    sendMessage("Enter Book Title\n"+Constants.SPACER);
                    title = reader.readLine();
                    sendMessage("Enter Book Author\n"+Constants.SPACER);
                    author = reader.readLine();
                    sendMessage("Enter Book Genre\n"+Constants.SPACER);
                    genre = reader.readLine();
                    sendMessage("Enter Book Price\n"+Constants.SPACER);
                    price = Double.parseDouble(reader.readLine());
                    if(Book.addBook(connection, title,author,genre,price,userController.getUser().userid))
                        sendMessage("Book added Succesfully!\n"+Constants.SPACER,false);
                    else
                        sendMessage("Error While Adding Book\n"+Constants.SPACER,false);
                }
                else if(choice.equals("2")){
                    
                } 
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

                System.out.println(userController.getUser().userName + " is logged in");

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
