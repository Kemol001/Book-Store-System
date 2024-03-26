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

            // while (true) {
                Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            // code goes here.
                            String text="";
                            // not eom and not exit = normal message
                            try {
                                while (!(text = reader.readLine()).equals(Constants.EOM) && !text.equals(Constants.EXIT)) {
                                    System.out.println(text);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (text.equals(Constants.EXIT)){
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }
                });  
                t1.start();

                Thread t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // code goes here.
                        while(true){
                            try {
                                writer.write(scanner.nextLine()+"\n");
                                writer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });  
                t2.start();

                t1.join();
                t2.stop();
                // t2.join();
            // }

            scanner.close();
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
