package com.ahujalimtamayo.project.common;

import com.ahujalimtamayo.project.model.Warrior;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    private MessageType messageType;
    private String message;
    private Warrior warrior;
    ActionMessage actionMessage;


    public ChatMessage(MessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public ChatMessage(MessageType messageType, ActionMessage actionMessage) {
        this.messageType = messageType;
        this.actionMessage = actionMessage;
    }

    public ChatMessage(MessageType messageType, String message, Warrior warrior) {
        this.messageType = messageType;
        this.message = message;
        this.warrior = warrior;
    }

    public MessageType getMessageType() { return messageType; }

    public String getMessage() { return message; }

    public Warrior getWarrior() { return warrior; }

    public ActionMessage getActionMessage() {
        return actionMessage;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageType=" + messageType +
                ", message='" + message + '\'' +
                ", warrior=" + warrior +
                ", actionMessage=" + actionMessage +
                '}';
    }
}

