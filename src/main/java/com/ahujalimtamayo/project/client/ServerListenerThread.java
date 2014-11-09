package com.ahujalimtamayo.project.client;

import com.ahujalimtamayo.project.common.ChatMessage;
import com.ahujalimtamayo.project.common.DisplayUtil;
import com.ahujalimtamayo.project.common.MessageSendingException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServerListenerThread extends Thread {

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

                System.out.println(chatMessage.getMessage());
                DisplayUtil.displayPrompt(username);

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
}
