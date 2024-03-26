package com.book.store.system.Handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.book.store.system.Constants;
import com.book.store.system.Entities.*;

public class UserHandler implements Handler{
    private final ClientHandler clientHandler;
    // private volatile boolean threadActive = true;
    public UserHandler(ClientHandler clientHandler){
        this.clientHandler = clientHandler;
    }

    public void removeBook() throws Exception{
        Map<String, String> arguments = new HashMap<>();
        arguments.put("owner_id",String.valueOf(clientHandler.userController.getUser().userId));
        arguments.put("borrower_id","null");
        ArrayList<Book> myBooks = Book.search(clientHandler.connection,arguments);
        clientHandler.sendMessage("Choose The ID Of The Book You Wish To Remove or "+Constants.EXIT+" to exit.\n"+Constants.SPACER,false);
        for(Book book : myBooks){
            clientHandler.sendMessage(book.bookId+")Title: "+book.title+" Author: "+book.author+" Genre: "+book.genre+
            " Price: "+book.price,false);
        }
        clientHandler.sendMessage("");
        String choice = clientHandler.reader.readLine();
        if(choice.equals(Constants.EXIT)) return;
        else if(Book.delete(clientHandler.connection,Integer.parseInt(choice),clientHandler.userController.getUser().userId))
            clientHandler.sendMessage("Book Removed Succesfully!\n"+Constants.SPACER,false);
        else
            clientHandler.sendMessage("Error While Removing Book\n"+Constants.SPACER,false);
    }


    public void addBook() throws Exception{
        String title,author,genre;
        double price;
        clientHandler.sendMessage("Enter Book Title:"); title = clientHandler.reader.readLine().toLowerCase();
        clientHandler.sendMessage("Enter Book Author:"); author = clientHandler.reader.readLine().toLowerCase();
        clientHandler.sendMessage("Enter Book Genre (comma separated):"); genre = clientHandler.reader.readLine().toLowerCase();
        clientHandler.sendMessage("Enter Book Price:"); price = Double.parseDouble(clientHandler.reader.readLine());
        if(Book.addBook(clientHandler.connection, title,author,genre,price,clientHandler.userController.getUser().userId))
            clientHandler.sendMessage("Book Added Succesfully!\n"+Constants.SPACER,false);
        else
            clientHandler.sendMessage("Error While Adding Book\n"+Constants.SPACER,false);
    }


    public void searchBooks() throws Exception{
        clientHandler.sendMessage("Enter The Category You Want To Search By And The Value (Space Seprated)\n"+
            "Available Categories (Title/Author/Genre/Price)\nAnd "+ Constants.EXIT +" to stop taking input\n"+ Constants.SPACER);
        String line;
        Map<String, String> attributes = new HashMap<>();
        while (!(line = clientHandler.reader.readLine().toLowerCase()).equals(Constants.EXIT)){
            String[] parts = line.split("\\s+", 2); // Split line into two parts (category and value)
            if (parts.length == 2) {
                String category = parts[0].trim().toLowerCase();
                String value = parts[1].trim().toLowerCase();
                attributes.put(category, value); // Add category and value to the map
            } else {
                // Handle invalid input
                clientHandler.sendMessage("Invalid input format. Please enter category and value separated by space.\n"+ Constants.SPACER,false);
            }
            clientHandler.sendMessage("");
        }
        ArrayList<Book> books = Book.search(clientHandler.connection,attributes);
        int i = 1;
        for(Book book : books){
            clientHandler.sendMessage("id: "+(i++)+" Title: "+book.title+" Author: "+book.author+" Genre: "+book.genre+
            " Price: "+book.price+" Book Owner: "+book.ownerName,false);
        }
        clientHandler.sendMessage(Constants.SPACER+"\nChoose The ID Of The Book You Want To Request or exit");
        while(!(line = clientHandler.reader.readLine().toLowerCase()).equals(Constants.EXIT)){
            int bookId = Integer.parseInt(line)-1;
            if(bookId < 0 || bookId > books.size()){
                clientHandler.sendMessage("Invalid Book ID\n"+Constants.SPACER);
                continue;
            }
            Book book = books.get(bookId);
            if(Request.addRequest(clientHandler.connection,clientHandler.userController.getUser().userId,book.ownerId,book.bookId)){
                clientHandler.sendMessage("Request Sent Succesfully!\n"+Constants.SPACER);
            }else{
                clientHandler.sendMessage("Error While Sending Request\n"+Constants.SPACER);
            }
        }
        
    }
    

    public void reviewRequests() throws Exception{
        ArrayList<Request> myRequests = Request.getUserRequests(clientHandler.connection,"owner",clientHandler.userController.getUser().userId);
        clientHandler.sendMessage(clientHandler.userController.getUser().userName+" Recieved Requests:\n"+Constants.SPACER,false);
        int i=0;
        for(Request request : myRequests){
            clientHandler.sendMessage((++i)+"- Book Name: "+request.bookTitle+" Borrower Name: "+
            request.borrowerName,false);
        }
        clientHandler.sendMessage("Type Request ID (Accept/Reject) Or Exit\n"+Constants.SPACER);
        String input = "";
        while(!(input = clientHandler.reader.readLine()).equals(Constants.EXIT)){
            String parts [] = input.split("\\s+",2);
            if(parts.length == 2){
                int requestID = Integer.parseInt(parts[0].trim())-1;
                String status = (parts[1].trim());
                if(requestID < 0 || requestID > myRequests.size()){
                    clientHandler.sendMessage("Invalid Request ID\n"+Constants.SPACER);
                }
                else if(status.toLowerCase().equals("accept") && Request.setStatus(clientHandler.connection,myRequests.get(requestID).requestId,status)){
                    clientHandler.sendMessage("Request Accepted Succesfully!\n"+Constants.SPACER);
                    openChat(myRequests.get(requestID).requestId,"owner");
                }
                else if(status.toLowerCase().equals("reject")  && Request.setStatus(clientHandler.connection,myRequests.get(requestID).requestId,status)){
                    clientHandler.sendMessage("Request Rejected Succesfully!\n"+Constants.SPACER);
                }
                else{
                    clientHandler.sendMessage("Invalid Input\n"+Constants.SPACER);
                }
            }
            else{
                clientHandler.sendMessage("Invalid Input Format\n"+Constants.SPACER); 
            }
        }
    }
    

    public void getRequestsHistory() throws Exception{
        ArrayList<Request> myRequests = Request.getUserRequests(clientHandler.connection,"borrower",clientHandler.userController.getUser().userId);
        clientHandler.sendMessage(clientHandler.userController.getUser().userName+" Submitted Requests:\n"+Constants.SPACER,false);
        for(Request request : myRequests){
            clientHandler.sendMessage("request id: "+request.requestId+" Book Name: "+request.bookTitle+" Owner Name: "+
            request.ownerName+" Status: "+request.status+" request date: "+request.date.toString(),false);
        }
        clientHandler.sendMessage(Constants.SPACER,false);
    }


    synchronized public void openChat(int requestId,String userType) throws Exception{
        // threadActive = true;
        ArrayList<Message> messages = Message.getMessages(clientHandler.connection,requestId,0);
        String name; 
        if(messages.size() == 0){
            clientHandler.sendMessage("No Messages Yet\n"+Constants.SPACER);
            name = userType.equals("owner")?Request.getRequest(clientHandler.connection,requestId).borrowerName:Request.getRequest(clientHandler.connection,requestId).ownerName;
        }else{
            name = userType.equals("owner")?messages.get(0).borrowerName:messages.get(0).ownerName;
        }
        clientHandler.sendMessage("Chatting With "+ name +"\n"+"Type Your Message or Exit\n"+Constants.SPACER,false);
        for(Message message : messages){
            clientHandler.sendMessage((message.senderType.equals(userType)?"me":name)+": "+message.message+" Date: "+message.date,false);
        }
        
        // while(true){
        //     try {
        //         ArrayList<Message> newMessages = Message.getMessages(clientHandler.connection,requestId,messages.get(messages.size()-1).messageId);
        //         if(newMessages.size() > 0){
        //             messages = newMessages;
        //             name = userType.equals("owner")?messages.get(0).borrowerName:messages.get(0).ownerName;
        //             for(Message message : messages){
        //                 if(!message.senderType.equals(userType))
        //                     clientHandler.sendMessage(name+": "+message.message+" Date: "+message.date,false);
        //             }
        //         }

        //         clientHandler.sendMessage("");
        //         String input = "";
        //         // ExecutorService executor = Executors.newSingleThreadExecutor();
        //         // Future<String> future = executor.submit(new InputReaderTask(clientHandler.reader));
        //         // try {
        //         //     input = future.get(5, TimeUnit.SECONDS); // Timeout set to 5 seconds
        //         // } catch (Exception e) {
                    
        //         // }

        //         input = clientHandler.reader.readLine() ;

        //         if(input.equals("."))
        //             continue;

        //         if(input.toLowerCase().equals(Constants.EXIT)){
        //             // executor.shutdownNow();
        //             return;
        //         } 

        //         if(!Message.addMessage(clientHandler.connection,requestId,input,userType)){
        //             clientHandler.sendMessage("Error While Sending Message\n"+Constants.SPACER);
        //         }

        //     } catch (Exception e) {
        //         e.printStackTrace();
        //         return;
        //     }
        // }


        try{
            
            Thread thread1 = new Thread(new Runnable()  {
                @Override
                public void run() {
                    ArrayList<Message> messages = Message.getMessages(clientHandler.connection,requestId,0);
                    String name; 
                    if(messages.size() == 0){
                        clientHandler.sendMessage("No Messages Yet\n"+Constants.SPACER);
                        name = userType.equals("owner")?Request.getRequest(clientHandler.connection,requestId).borrowerName:Request.getRequest(clientHandler.connection,requestId).ownerName;
                    }else{
                        name = userType.equals("owner")?messages.get(0).borrowerName:messages.get(0).ownerName;
                    }
                    clientHandler.sendMessage("Chatting With "+ name +"\n"+"Type Your Message or Exit\n"+Constants.SPACER,false);
                    for(Message message : messages){
                        clientHandler.sendMessage((message.senderType.equals(userType)?"me":name)+": "+message.message+" Date: "+message.date,false);
                    }
                    while(true){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(messages.size()==0) ;
                        ArrayList<Message> newMessages = Message.getMessages(clientHandler.connection,requestId,(messages.size()==0 ? 0 :messages.get(messages.size()-1).messageId));
                        if(newMessages.size() > 0){
                            messages = newMessages;
                            name = userType.equals("owner")?messages.get(0).borrowerName:messages.get(0).ownerName;
                            for(Message message : messages){
                                if(!message.senderType.equals(userType))
                                    clientHandler.sendMessage(name+": "+message.message+" Date: "+message.date,false);
                            }
                        }
                    }
                    
                }
            });  

            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        clientHandler.sendMessage("");
                        String input = "";
                        try {
                            input = clientHandler.reader.readLine() ;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(input.toLowerCase().equals(Constants.EXIT)){
                            Thread.currentThread().interrupt();
                            // threadActive = false;
                            return;
                        } 

                        if(!Message.addMessage(clientHandler.connection,requestId,input,userType)){
                            clientHandler.sendMessage("Error While Sending Message\n"+Constants.SPACER);
                        }
                    }
                }
            });  
            

            // Starting both threads
            thread1.start();
            thread2.start();

            thread2.join();
            // Waiting for both threads to finish
            // while(threadActive){
            //     Thread.sleep(1000);
            // }

            thread1.stop();
            thread2.stop();
            
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    static class InputReaderTask implements Callable<String> {
        private BufferedReader reader;

        public InputReaderTask(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public String call() throws IOException {
            return reader.readLine();
        }
    }


    public void getChatList() throws Exception{
        while(true){
            clientHandler.sendMessage("Chat List\n"+Constants.SPACER,false);
            ArrayList<Request> myRequests = Request.getAcceptedRequests(clientHandler.connection,clientHandler.userController.getUser().userId);
            
            int i=1;
            for(Request request : myRequests){
                clientHandler.sendMessage("Chat ID: "+(i++)+" Book Name: "+request.bookTitle+" Owner Name: "+
                request.ownerName+" Borrower Name: "+request.borrowerName,false);
            }
            clientHandler.sendMessage("Type request Id or Exit\n"+Constants.SPACER);
            String input = (clientHandler.reader.readLine());
    
            if(input.equals(Constants.EXIT)) break;
            int chatId = Integer.parseInt(input)-1;
            if(!(chatId < 0 || chatId > myRequests.size())){
                openChat(myRequests.get(chatId).requestId, clientHandler.userController.getUser().userId == myRequests.get(chatId).ownerId?"owner":"borrower");
                clientHandler.sendMessage("Chat closed\n"+Constants.SPACER,false);
            }else{
                clientHandler.sendMessage("Invalid Chat ID\n"+Constants.SPACER,false);
            }
        }
        // openChat(2, "owner");
        // Request.getUserRequests(null, null, 0);
        // Message.getMessages(clientHandler.connection, 2,0);
    }


    public void start() throws Exception{
        clientHandler.sendMessage("Choose an Action\n" + Constants.SPACER, false);
        while (true) {
            clientHandler.sendMessage("1- Browse And Search Books \n2- Add Or Remove Books\n" +
            "3- Review Requests\n4- View Request History\n5- Messaging\n6- Logout\n"+ Constants.SPACER);
            String inputLine = clientHandler.reader.readLine();
            if (inputLine.equals("1")) {
                searchBooks();
            }
            else if(inputLine.equals("2")){
                clientHandler.sendMessage("1- Add a book\n2- Remove A Book\n"+ Constants.SPACER);
                String choice = clientHandler.reader.readLine();
                if(choice.equals("1")){
                    addBook();
                }
                else if(choice.equals("2")){
                    removeBook();
                } 
            }
            else if(inputLine.equals("3")){
                reviewRequests();
            }
            else if(inputLine.equals("4")){
                getRequestsHistory();
            }
            else if(inputLine.equals("5")){
                getChatList();
            }
            else if(inputLine.equals("6")){
                clientHandler.sendMessage("See You soon!\n"+Constants.SPACER,false);
                clientHandler.userController.logout();
                break;
            }else{
                clientHandler.sendMessage("Invalid Input!!!\n"+Constants.SPACER,false);
            }
        }
    }
}
