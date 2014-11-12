package com.ahujalimtamayo.project.client;

import com.ahujalimtamayo.project.common.*;
import com.ahujalimtamayo.project.model.Warrior;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WarriorClient {

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private String server;
    private String username;
    private int port;
    ServerListenerThread serverListener;

    public WarriorClient(ConnectionInformation connectionInformation) {
        this.server = connectionInformation.getServerAddress();
        this.port = connectionInformation.getPortNumber();
        this.username = connectionInformation.getUserName();
    }

    public boolean initialize() {

        try {
            initializeSocket();

            initializeInputOutputObjectStreams();

            serverListener = new ServerListenerThread(username, inputStream, outputStream);

            serverListener.start();


        } catch (ServerConnectionErrorException e) {
            System.out.println("Error connecting to server:" + e);
            return false;
        } catch (IOStreamCreationException e) {
            System.out.println("error creating new Input/output Streams: " + e);
            return false;
        } catch (MessageSendingException e) {
            System.out.println("error thrown when user sends login name : " + e);
            serverListener.setRunning(false);
            disconnect();
            return false;
        }

        return true;
    }

    public void sendMessage(ChatMessage msg) {
        try {
            outputStream.writeObject(msg);
        } catch (IOException e) {
            System.out.println("Exception writing to server: " + e);
        }
    }

    private void initializeSocket() throws ServerConnectionErrorException {
        try {
            socket = new Socket(server, port);

            System.out.println(String.format("Connection accepted %s : %s", socket.getInetAddress(), socket.getPort()));

        } catch (IOException e) {
            throw new ServerConnectionErrorException();
        }
    }

    private void initializeInputOutputObjectStreams() throws IOStreamCreationException {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new IOStreamCreationException();
        }
    }

    public void disconnect() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.println("Error closing the socket and streams" + e);
        }

    }

    public String getUsername() {
        return username;
    }


    public boolean processInputMessage(String userInputMessage) {
        if (userInputMessage.equalsIgnoreCase(MessageType.LOGOUT.getShortValue())) {

            sendMessage(new ChatMessage(MessageType.LOGOUT, ""));
            return true;

        } else if (userInputMessage.contains(MessageType.LOAD_WARRIOR.getShortValue())) {

            loadWarrior(userInputMessage);

        } else if (userInputMessage.contains(MessageType.HELP.getShortValue())) {

            DisplayUtil.displayHelp();

        } else if (userInputMessage.contains(MessageType.WHOISIN.getShortValue())) {

            processWhoIsInCommand(userInputMessage);

        } else if (userInputMessage.contains(MessageType.ATTACK.getShortValue())) {

            processActionCommand(userInputMessage, MessageType.ATTACK);

        } else if (userInputMessage.contains(MessageType.DEFEND.getShortValue())) {

            processActionCommand(userInputMessage, MessageType.DEFEND);

        } else if (userInputMessage.contains(MessageType.STATISTIC.getShortValue())) {

            displayWarriorStatistic();

        } else {
            sendMessage(new ChatMessage(MessageType.MESSAGE, userInputMessage));
        }
        return false;
    }


    private void loadWarrior(String message) {
        String path = extractValue(message);

        if (StringUtils.isNotBlank(path)) {
            try {
                Warrior warrior = XmlUtil.readWarriorFromFile(path);

                sendMessage(new ChatMessage(MessageType.LOAD_WARRIOR, "", warrior));
            } catch (IOException e) {
                DisplayUtil.displayEvent("error loading warrior. please check the xml syntax of your file.");
            }
        } else {
            DisplayUtil.displayEvent("You need to specify the fully qualified path of the warrior.");
        }

    }

    private String extractValue(String message) {
        String[] messageValue = StringUtils.strip(message).split(" ");

        if (messageValue.length > 1) {
            System.out.println(messageValue[1]);
            return messageValue[1];
        } else {
            return "";
        }
    }

    private void processWhoIsInCommand(String userInputMessage) {
        String warriorName = extractValue(userInputMessage);

        sendMessage(new ChatMessage(MessageType.WHOISIN, warriorName));
    }


    private void processActionCommand(String userInputMessage, MessageType messageType) {

        if (getWarrior() == null) {
            DisplayUtil.displayWarriorNotFound();
            return;
        }

        ActionMessage actionMessage = extractActionMessage(userInputMessage, messageType);

        if (actionMessage != null) {

            if (isAttackMessageValid(actionMessage, messageType)) {
                sendMessage(new ChatMessage(messageType, actionMessage));
            }

        } else {
            DisplayUtil.displayInvalidUseOfAttackCommand();
        }
    }

    private ActionMessage extractActionMessage(String message, MessageType messageType) {
        String[] messageValue = StringUtils.strip(message).split(" ");

        if (messageValue.length > 2) {
            int attackPoint = extractActionPoints(messageType, messageValue[2]);

            return new ActionMessage(messageValue[1], messageValue[2], attackPoint);

        } else {
            return null;
        }

    }

    private int extractActionPoints(MessageType messageType, String actionName) {
        int attackPoint = 0;
        if (messageType == MessageType.ATTACK) {
            attackPoint = getWarrior().findAttackPoint(actionName);
        } else if (messageType == MessageType.DEFEND) {
            attackPoint = getWarrior().findDefensePoint(actionName);
        }
        return attackPoint;
    }


    private boolean isAttackMessageValid(ActionMessage actionMessage, MessageType messageType) {

        boolean isActionAvailable = isActionAvailable(actionMessage, messageType);


        if (StringUtils.equals(actionMessage.getWarriorName(), getWarrior().getName())) {

            DisplayUtil.displayCannotDoActionError(messageType);

            return false;
        }

        if (!isActionAvailable) {

            DisplayUtil.displayAttackNotAvailable(messageType);
            return false;

        }
        return true;
    }


    private boolean isActionAvailable(ActionMessage actionMessage, MessageType messageType) {
        return messageType == MessageType.ATTACK ? getWarrior().isAttackAvailable(actionMessage.getActionName()) : getWarrior().isDefenseAvailable(actionMessage.getActionName());
    }

    private void displayWarriorStatistic() {
        if (getWarrior() != null) {
            DisplayUtil.displayEvent(getWarrior().toString());
        } else {
            DisplayUtil.displayEvent("You have not loaded any warrior.");
        }
    }


    public Warrior getWarrior() { return serverListener.getWarrior(); }

}
