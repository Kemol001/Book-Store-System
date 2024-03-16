package com.book.store.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BookStoreClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        
            System.out.print("Enter your username: ");
            String userName = reader.readLine();
            writer.println(userName);

            System.out.println("Connected to the auction server. Type 'exit' to leave the auction.");

            while (true) {
                System.out.print("Enter your bid (or type 'exit' to leave): ");
                String bidInput = reader.readLine();

                if (bidInput.equalsIgnoreCase("exit")) {
                    break;
                }

                try {
                    int bid = Integer.parseInt(bidInput);
                    writer.println(bid);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid bid format. Please enter a valid number.");
                }

            }
            
            socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
