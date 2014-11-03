package com.ahujalimtamayo.project.common;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    public enum MessageType { WHOISIN, MESSAGE, LOGOUT};

    private MessageType messageType;
    private String message;


    public ChatMessage(MessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public MessageType getMessageType() { return messageType; }

    public String getMessage() { return message; }
}

