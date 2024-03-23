package com.book.store.system;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.book.store.system.Db.Db;
import com.book.store.system.Handlers.*;


public class BookStoreServer {
    private static final int PORT = 5000;
    private static final List<ClientHandler> clients = new ArrayList<>();
    
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;

        try {
            
            if(!Db.init()){
                System.out.println("Error while initializing the database");
                return;
            }
            
            serverSocket = new ServerSocket(PORT);
            System.out.println("Book Store Server is running on port : " + PORT);


            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}