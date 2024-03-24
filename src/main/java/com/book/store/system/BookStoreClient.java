package com.book.store.system;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BookStoreClient {
    public static void main(String[] args) {

        String hostname = "localhost";
        int port = 5000;

        try {
            Socket socket = new Socket(hostname, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner scanner = new Scanner(System.in);

            while (true) {
            
                String text="";
               // not eom and not exit = normal message
                while (!(text = reader.readLine()).equals(Constants.EOM) && !text.equals(Constants.EXIT)) {
                    System.out.println(text);
                }

                if (text.equals(Constants.EXIT))
                    break;
                    
                writer.write(scanner.nextLine()+"\n");
                writer.flush();

            }

            scanner.close();
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
