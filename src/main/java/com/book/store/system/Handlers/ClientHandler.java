package com.book.store.system.Handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;

import com.book.store.system.Constants;
import com.book.store.system.Controllers.UserController;
import com.book.store.system.Database.Db;

public class ClientHandler implements Runnable, Handler{

    public final Socket clientSocket;
    public final BufferedWriter writer;
    public final BufferedReader reader;
    public Connection connection;
    public UserController userController = new UserController();

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.connection = Db.connect();
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendMessage(String message, boolean waitUserInput) {
        try {
            String eom = waitUserInput ? Constants.EOM + "\n" : "";
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


    public void register() throws Exception{
        sendMessage("Enter your username: "); String userName = reader.readLine();
        sendMessage("Enter your password:"); String password = reader.readLine();
        sendMessage("Enter Role (admin/user): ");
        String role = reader.readLine();
        if (!role.equals("admin") && !role.equals("user")) {
            sendMessage("Invalid role!!!\n" + Constants.SPACER, false);
            return;
        }
        if (userController.register(connection,userName, password, role) != Constants.ERROR) {
            sendMessage("User registered successfully!\n" + Constants.SPACER, false);
        } else {
            sendMessage("Error " + Constants.ERROR + " while registering the user!\n" + Constants.SPACER,false);
        }
    }


    public boolean login() throws Exception{
        sendMessage("Enter your username: "); String userName = reader.readLine();
        sendMessage("Enter your password:"); String password = reader.readLine();
        int result = userController.login(connection,userName, password);
        if (result == Constants.SUCCESS) {
            sendMessage(Constants.SPACER+"\nWelcome " + userName + "!\n" + Constants.SPACER,false);
            return true;
        } else if (result == Constants.USER_NOT_FOUND) {
            sendMessage("Error " + Constants.USER_NOT_FOUND + " User not found!!!\n" + Constants.SPACER, false);
        } else if (result == Constants.WRONG_PASSWORD) {
            sendMessage("Error " + Constants.WRONG_PASSWORD + " Wrong password!!!\n" + Constants.SPACER, false);
        } else {
            sendMessage("Error " + Constants.ERROR + " while logging in!\n" + Constants.SPACER, false);
        }
        return false;
    }


    public void start() throws Exception {
        sendMessage("Welcome to the Book Store!\n" + Constants.SPACER, false);
        while (true) {
            sendMessage("1- Register\n2- Login\n3- Exit\n" + Constants.SPACER); String inputLine = reader.readLine();
            if (inputLine.equals("3")) {
                sendMessage("Goodbye!\n" + Constants.SPACER +"\n" + Constants.EXIT);
                break;
            }
            if (inputLine.equals("1")) {
                register();
            } else if (inputLine.equals("2")) {
                if(login()) break;
            } else {
                sendMessage("Invalid input!!!\n" + Constants.SPACER,false); 
            }
        }

    }


    @Override
    public void run() {
        try {

            while (true) {
                start();

                if (!userController.isLoggedIn()) {
                    break;
                }

                if(userController.getUser().userType.equals("admin")){
                    AdminHandler adminHandler = new AdminHandler(this);
                    adminHandler.start();
                }else{
                    UserHandler userHandler = new UserHandler(this);
                    userHandler.start();
                }

            }

            clientSocket.close();
            connection.close();
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
