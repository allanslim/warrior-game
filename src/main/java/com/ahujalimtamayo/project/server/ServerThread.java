package com.ahujalimtamayo.project.server;

import com.ahujalimtamayo.project.common.ActionMessage;
import com.ahujalimtamayo.project.common.ChatMessage;
import com.ahujalimtamayo.project.common.DisplayUtil;
import com.ahujalimtamayo.project.common.MessageType;
import com.ahujalimtamayo.project.model.Warrior;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ServerThread extends Thread {

    private Socket socket;

    private ObjectInputStream inputStream;

    private ObjectOutputStream outputStream;

    private int threadId;

    private String username;

    private String dateInString;

    private Warrior warrior;

    private List<ServerThread> serverThreads;

    private volatile boolean isRunning = true;

    private SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm:ss");


    public ServerThread(Socket socket, int threadId, List<ServerThread> serverThreads) throws IOException, ClassNotFoundException {
        this.socket = socket;
        this.threadId = threadId;
        this.serverThreads = serverThreads;

        initializeInputOutputStreams(socket);

        username = (String) inputStream.readObject();

        DisplayUtil.displayEvent(username + " just connected.");
        dateInString = new Date().toString();
    }

    private void initializeInputOutputStreams(Socket socket) throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }


    @Override
    public void run() {

        while (isRunning) {
            try {
                ChatMessage chatMessage = (ChatMessage) inputStream.readObject();

                switch (chatMessage.getMessageType()) {

                    case MESSAGE:
                        broadcastMessageToAllClients(username + ": " + chatMessage.getMessage());
                        break;
                    case LOGOUT:
                        DisplayUtil.displayEvent(username + " disconnected with a LOGOUT message.");
                        isRunning = false;
                        break;
                    case LOAD_WARRIOR:
                        Warrior currentWarrior = chatMessage.getWarrior();
                        if(!doesWarriorExist(currentWarrior)) {
                            warrior = currentWarrior;
                            sendWarriorLoadedMessage(warrior);
                            broadcastMessageToAllClients(username + " loaded: " + warrior.getName());
                        } else {
                            sendWarriorAlreadyExistMessage(currentWarrior.getName());
                        }
                        break;
                    case ATTACK:
                        processAction(chatMessage, MessageType.ATTACK);
                        break;
                    case DEFEND:
                        processAction(chatMessage, MessageType.DEFEND);
                    case WHOISIN:
                        displayWarriorInfo(chatMessage.getMessage());
                        break;
                }
            } catch (Exception e) {
                DisplayUtil.displayEvent(username + " Exception reading Streams: " + e);
                break;
            }
        }

        // remove myself from the arrayList containing the list of the
        // connected Clients
        removeClientThreadFromListById(threadId);

        closeAllResource();
    }

    private synchronized void sendWarriorAlreadyExistMessage(String warriorName ) {
        ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE, "A player already loaded a Warrior with this name: [" + warriorName + "]. Please choose a different warrior.");
        writeMsg(chatMessage);
    }

    private synchronized  boolean doesWarriorExist(Warrior warrior) {

        for (int i = serverThreads.size(); --i >= 0; ) {
            ServerThread serverThread = serverThreads.get(i);

            if(serverThread.getThreadId() != threadId) {
                if(serverThread.getWarrior() != null && StringUtils.equals(serverThread.getWarrior().getName(), warrior.getName())) {
                    return true;
                }
            }
        }
        return false;
    }


    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    private void displayWarriorInfo(String warriorName) {

        writeMsg(buildChatMessage("\n========= List of the users connected at " + displayTime.format(new Date()) + " =========\n" ));

        for (int i = 0; i < serverThreads.size(); i++) {
            ServerThread ct = serverThreads.get(i);

            if (ct.warrior != null) {

                writeMsg(buildChatMessage(((i + 1) + ") User:" + ct.username + " connected since " + ct.dateInString + " has Warrior: " + ct.warrior.getName() + "\n")));

                if (StringUtils.equals(ct.warrior.getName(), warriorName)) {
                    writeMsg(buildChatMessage(ct.warrior.toString()));
                }
            } else {
                writeMsg(buildChatMessage((i + 1) + ") User:" + ct.username + " connected since " + ct.dateInString));
            }

        }
    }

    private ChatMessage buildChatMessage(String message) {
        return new ChatMessage(MessageType.MESSAGE, message) ;
    }

    private void processAction(ChatMessage chatMessage,  MessageType messageType) {

        ActionMessage actionMessage = chatMessage.getActionMessage();

        ServerThread targetServerThread = findClient(actionMessage);

        if(targetServerThread != null ) {

            if(messageType == MessageType.MESSAGE.ATTACK) {
                targetServerThread.getWarrior().reduceHealthPoints(actionMessage.getActionPoint());
            }else if(messageType == MessageType.DEFEND) {
                targetServerThread.getWarrior().addHealthPoints(actionMessage.getActionPoint());
            }

        }

        sendMessageToClient(targetServerThread, actionMessage, messageType);

        sendActionNotifyMessage(targetServerThread, actionMessage, messageType);
    }



    private ServerThread findClient(ActionMessage actionMessage) {

        for (int i = serverThreads.size(); --i >= 0; ) {
            ServerThread serverThread = serverThreads.get(i);

            Warrior currentWarrior = serverThread.getWarrior();

            if(StringUtils.equals(currentWarrior.getName(), actionMessage.getWarriorName())) {
                return serverThread;
            }
        }
        return null;
    }

    private void sendActionNotifyMessage(ServerThread targetServerThread, ActionMessage actionMessage, MessageType messageType) {

        MessageType actionNotifyMessageType = messageType == MessageType.ATTACK ? MessageType.ATTACK_NOTIFY : MessageType.DEFEND_NOTIFY;

        ChatMessage chatMessage = new ChatMessage(actionNotifyMessageType, actionMessage);

        sendToClient(targetServerThread, chatMessage,  targetServerThread.getUsername());
    }


    private synchronized void sendMessageToClient(ServerThread targetServerThread, ActionMessage actionMessage, MessageType messageType) {

        String message = extractActionMessage( actionMessage, messageType);

        ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE, message);

        sendToClient(targetServerThread, chatMessage, targetServerThread.getUsername());

    }

    private String extractActionMessage( ActionMessage actionMessage, MessageType messageType) {
        String message = "";
        if(messageType == MessageType.ATTACK) {
            message = actionMessage.getWarriorName() + " is attacking your warrior " + getWarrior().getName() + " with " + actionMessage.getActionName();
        }else if(messageType == MessageType.DEFEND) {
            message = this.getWarrior().getName() + " is defending from your attack  with " + actionMessage.getActionName();
        }
        return message;
    }

    private synchronized  void sendToClient(ServerThread targetServerThread, ChatMessage chatMessage, String playername) {

        if(targetServerThread != null && !targetServerThread.writeMsg(chatMessage)) {

            removeClientThreadFromListByIdByPlayerName(playername);

            DisplayUtil.displayEvent("Disconnected Client " + targetServerThread.username + " removed from list.");
        }
    }

    private void removeClientThreadFromListByIdByPlayerName(String playerName) {

        for (int i = serverThreads.size(); --i >= 0; ) {
            ServerThread serverThread = serverThreads.get(i);

            if(StringUtils.equals(serverThread.getUsername(), playerName)) {
                serverThreads.remove(i);
            }
        }
    }


    private synchronized void sendWarriorLoadedMessage(Warrior warrior) {

        ChatMessage chatMessage = new ChatMessage(MessageType.WARRIOR_LOADED, "Warrior loaded successfully", warrior);

        ServerThread serverThread = findClientById(getThreadId());

        serverThread.writeMsg(chatMessage);
    }

    private synchronized void broadcastMessageToAllClients(String message) {

        ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE, message);

        DisplayUtil.displayBroadcastMessage(chatMessage);


        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for (int i = serverThreads.size(); --i >= 0; ) {
            ServerThread serverThread = serverThreads.get(i);
            // try to write to the Client if it fails removeClientThreadFromListById it from the list
            if (!serverThread.writeMsg(chatMessage)) {
                serverThreads.remove(i);
                DisplayUtil.displayEvent("Disconnected Client " + serverThread.username + " removed from list.");
            }
        }
    }

    // for a client who logoff using the LOGOUT message
    private synchronized void removeClientThreadFromListById(int id) {

        for (int i = 0; i < serverThreads.size(); ++i) {

            ServerThread serverThread = serverThreads.get(i);
            // found it
            if (serverThread.threadId == id) {
                serverThreads.remove(i);
                return;
            }
        }
    }


    private synchronized ServerThread findClientById(int threadId) {
        for (int i = 0; i < serverThreads.size(); ++i) {

            ServerThread serverThread = serverThreads.get(i);
            // found it
            if (serverThread.threadId == threadId) {
                return serverThread;
            }
        }
        return null;
    }


    public void closeAllResource() {
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            DisplayUtil.displayEvent(username + " Error closing resources: " + e);
        }
    }

    private boolean writeMsg(ChatMessage chatMessage) {

        if (!socket.isConnected()) {
            closeAllResource();
            return false;
        }

        try {

            outputStream.writeObject(chatMessage);

        } catch (IOException e) {
            // if an error occurs, do not abort just inform the user
            DisplayUtil.displayEvent("Error sending message to " + username);
            DisplayUtil.displayEvent(e.toString());
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public Warrior getWarrior() { return warrior; }

    public int getThreadId() { return threadId; }
}
