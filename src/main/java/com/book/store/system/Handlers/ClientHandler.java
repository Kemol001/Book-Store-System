package com.book.store.system.Handlers;

import java.io.*;
import java.net.*;

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


    @Override
    public void run() {
        try{
            writer.write("Welcome to the Book Store!\n==========\n");
            while(true){
                writer.write("1- Register\n2- Login\n3- Exit\n==========\n");
                writer.flush();
                String inputLine = reader.readLine();
                writer.write("Enter your username: ");
                writer.flush();
                String userName = reader.readLine();
                writer.write("Enter your password: ");
                writer.flush();
                String password = reader.readLine();
                if(inputLine.equals("1")){
                    // register(userName, password);
                } else if(inputLine.equals("2")){
                    // login(userName, password);
                } else if(inputLine.equals("3")){
                    writer.write("Goodbye!");
                    writer.flush();
                    break;
                } else {
                    writer.write("Invalid input\n==========\n");
                    writer.flush();
                }
                System.out.println("New user: " + userName);
                writer.write("\n");
            }
            clientSocket.close();
            return;
        } catch (IOException e) {
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

