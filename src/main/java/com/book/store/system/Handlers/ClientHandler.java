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
                if (userController.register(userName, password, role) != Constants.ERROR) {
                    sendMessage("User registered successfully!\n" + Constants.SPACER, false);
                } else {
                    sendMessage("Error " + Constants.ERROR + " while registering the user!\n" + Constants.SPACER,
                            false);
                }
            } else if (inputLine.equals("2")) {
                int result = userController.login(userName, password);
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
