package com.ahujalimtamayo.project.client;

import com.ahujalimtamayo.project.common.ActionMessage;
import com.ahujalimtamayo.project.common.ChatMessage;
import com.ahujalimtamayo.project.common.DisplayUtil;
import com.ahujalimtamayo.project.common.MessageSendingException;
import com.ahujalimtamayo.project.model.Warrior;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServerListenerThread extends Thread {

    private Warrior warrior;
    private String username;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private volatile boolean isRunning;

    public ServerListenerThread(String username, ObjectInputStream inputStream, ObjectOutputStream outputStream) throws MessageSendingException {
        this.username = username;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        isRunning = true;

        sendUsernameToServer();
    }


    @Override
    public void run() {
        while (isRunning) {
            try {
                ChatMessage chatMessage = (ChatMessage) inputStream.readObject();

                ActionMessage actionMessage = null;
                switch (chatMessage.getMessageType()) {

                    case MESSAGE:
                        DisplayUtil.displayEvent(chatMessage.getMessage());
                        DisplayUtil.displayPrompt(username);
                        break;
                    case ATTACK_NOTIFY:
                        actionMessage = chatMessage.getActionMessage();
                        warrior.reduceHealthPoints(actionMessage.getActionPoint());
                        break;
                    case DEFEND_NOTIFY:
                        actionMessage = chatMessage.getActionMessage();
                        warrior.addHealthPoints(actionMessage.getActionPoint());
                        break;
                    case WARRIOR_LOADED:
                        warrior = chatMessage.getWarrior();
                        DisplayUtil.displayEvent(String.format("warrior [%s] loaded.", warrior));
                        break;
                    case WARRIOR_DEATH_NOTIFY:
                        DisplayUtil.displayEvent(String.format("warrior [%s] is dead.", warrior.getName()));
                        warrior = null;
                        break;
                }
            } catch (Exception e) {
                System.out.println("Server has close the connection: " + e);
                break;
            }
        }
    }


    private void sendUsernameToServer() throws MessageSendingException {
        try {
            outputStream.writeObject(username);
        } catch (IOException eIO) {
            throw new MessageSendingException();
        }
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setWarrior(Warrior warrior) {
        this.warrior = warrior;
    }

    public Warrior getWarrior() { return warrior; }
}
