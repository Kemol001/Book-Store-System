package com.book.store.system;

import java.io.*;
import java.net.*;

public class BookStoreClient {
    public static void main(String[] args) {

        String hostname = "localhost";
        int port = 5000;

        try {
            Socket socket = new Socket(hostname, port);
            
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("*:5:10");
            writer.newLine();
            writer.flush();

            // get the result from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            System.out.println("The result sent from the server : " + reader.readLine());
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
