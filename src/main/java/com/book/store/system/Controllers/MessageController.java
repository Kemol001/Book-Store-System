package com.book.store.system.Controllers;

import java.util.ArrayList;
import java.util.List;

import com.book.store.system.Handlers.MessageHandler;

public class MessageController {

    private static final List<MessageHandler> messageHandlers = new ArrayList<>();

    private MessageController(){

    }
    
    public static void addHandler(MessageHandler messageHandler){
        messageHandlers.add(messageHandler);
    }

    public static MessageHandler getHandler(int requestId){
        for(MessageHandler messageHandler : messageHandlers){
            if(messageHandler.requestId==(requestId)){
                return messageHandler;
            }
        }
        return null;
    }
    
}
