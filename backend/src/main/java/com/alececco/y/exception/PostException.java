package com.alececco.y.exception;

public class PostException extends RuntimeException {

    private final String clientMessage;

    public PostException(String clientMessage, String internalMessage) {
        super(internalMessage);
        this.clientMessage = clientMessage;
    }
    
    public String getClientMessage() {
        return clientMessage;
    }
}
