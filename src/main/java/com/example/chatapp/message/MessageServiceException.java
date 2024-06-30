package com.example.chatapp.message;

public class MessageServiceException extends Throwable{
    public MessageServiceException() {
        super("Something went wrong in MessageService");
    }

}
