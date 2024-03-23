package com.book.store.system.Handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.book.store.system.Constants;
import com.book.store.system.Controllers.UserController;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private UserController userController = new UserController();


    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }


    public void sendMessage(String message){
        try {
            writer.write(message+"\n"+Constants.EOM+"\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startPage() throws Exception{
        sendMessage("Welcome to the Book Store!\n==========");
        while(true){
            sendMessage("1- Register\n2- Login\n3- Exit\n==========");
            String inputLine = reader.readLine();
            if(inputLine.equals("3")){
                sendMessage("Goodbye!\n==========");
                break;
            }
            sendMessage("Enter your username: ");
            String userName = reader.readLine();
            sendMessage("Enter your password:");
            String password = reader.readLine();
            if(inputLine.equals("1")){
                sendMessage("Enter Role (admin/user): ");
                String role = reader.readLine();
                if(userController.register(userName, password,role)!=Constants.ERROR){
                    sendMessage("User registered successfully!\n==========");
                } else {
                    sendMessage("Error "+Constants.ERROR+" while registering the user!\n==========");
                }
            } else if(inputLine.equals("2")){
                int result = userController.login(userName, password);
                if(result == Constants.SUCCESS){
                    sendMessage("Welcome "+userName+"!\n==========");
                    break;
                } else if(result == Constants.USER_NOT_FOUND){
                    sendMessage("Error "+Constants.USER_NOT_FOUND+"User not found!!!\n==========");
                } else if(result == Constants.WRONG_PASSWORD){
                    sendMessage("Error "+Constants.WRONG_PASSWORD+"Wrong password!!!\n==========");
                } else {
                    sendMessage("Error "+Constants.ERROR+" while logging in!\n==========");
                }

            } else {
                sendMessage("Invalid input!!!\n==========");
            }
        }

    }

    @Override
    public void run() {
        try{

            while(true){
                startPage();
    
                if(!userController.isLoggedIn()){
                    break;
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

