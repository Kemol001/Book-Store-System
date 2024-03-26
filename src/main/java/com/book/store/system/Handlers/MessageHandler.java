package com.book.store.system.Handlers;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import com.book.store.system.Controllers.UserController;
import com.book.store.system.Database.Db;
import com.book.store.system.Entities.Message;

public class MessageHandler implements Handler ,Runnable {

    public ClientHandler user1;
    public ClientHandler user2;
    public int requestId;

    private final LinkedBlockingQueue<Message> MessageQueue = new LinkedBlockingQueue<>();

    private boolean chatEnded = false;

    public MessageHandler(ClientHandler user,String userType,int requestID){
        this.requestId = requestID;
        if(!(userType.equals(null)) && userType.equals("borrower"))
            this.user1 = user;

        else if(!(userType.equals(null)) && userType.equals("owner"))
            this.user2 = user;
    }

    @Override
    public void start() throws Exception {
        run();
    }

    @Override
    public void run() {
        Thread writer = new Thread(this::sendMessages);
        writer.start();
    }

    // Method to send messages from both users to the original message queue
    synchronized private void sendMessages() {
        while (!chatEnded) {
            try {
                String input = user1.reader.readLine();
                if(!input.equals(null)){
                    String date = new Date(System.currentTimeMillis()).toString();
                    receiveMessages(user1,user2,input,date);
                    Message message = new Message(requestId,input,"borrower",date);
                    try {
                        MessageQueue.put(message);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                String input2 = user2.reader.readLine();
                if(!input.equals(null)){
                    String date = new Date(System.currentTimeMillis()).toString();
                    receiveMessages(user2,user1,input2,date);
                    Message message = new Message(requestId,input,"owner",date);
                    try {
                        MessageQueue.put(message);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        endChat();
    }

    // Method to receive messages from the original message queue and extract message content
    synchronized private void receiveMessages(ClientHandler sender,ClientHandler receiver,String message,String date) {
        try {
            receiver.writer.write(sender.userController.getUser().userName+"\n"+message+" "+date);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    // Method to flush all message objects in the database after the chat ends
    public void flushMessagesToDatabase() {
        while (!MessageQueue.isEmpty()) {
            Message message = MessageQueue.poll();
            // Store message content in the database
            Message.addMessage(null, message.requestId, message.getMessageString(), message.senderType);
        }
        System.out.println("All messages flushed to the database.");
    }

    // Method to end the chat and flush messages to the database
    public void endChat() {
        chatEnded = true;
        flushMessagesToDatabase();
    }

}
