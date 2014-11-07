package com.ahujalimtamayo.project.client;

import com.ahujalimtamayo.project.common.*;
import com.ahujalimtamayo.project.model.Warrior;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class WarriorClient {


    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private String server, username;
    private int port;

    private static Warrior warrior;


    public WarriorClient(ConnectionInformation connectionInformation) {
        this.server = connectionInformation.getServerAddress();
        this.port = connectionInformation.getPortNumber();
        this.username = connectionInformation.getUserName();
    }

    /*
     * To execute the Client in console mode use one of the following command
     * > java -jar warrior-client.jar
     * > java -jar warrior-client.jar  username
     * > java -jar warrior-client.jar  username portNumber
     * > java -jar warrior-client.jar  username portNumber serverAddress
     *
     * at the console prompt
     * If the portNumber is not specified 1500 is used
     * If the serverAddress is not specified "localHost" is used
     * If the username is not specified "Anonymous" is used
     *
     */
    public static void main(String[] args) {
        // default values

        ConnectionInformation connectionInformation = initConnectionInformation(args);

        if (connectionInformation == null) {
            return;
        }

        WarriorClient client = new WarriorClient(connectionInformation);

        if (!client.execute())
            return;

        Scanner scan = new Scanner(System.in);

        DisplayUtil.displayHelp();

        while (true) {
            System.out.print("> ");

            String userInputMessage = scan.nextLine();

            if (processInputMessage(client, userInputMessage)) break;
        }

        client.disconnect();
    }



    public boolean execute() {
        try {
            connectToServer();

            createInputOutStreams();

            new ListenFromServer().start();

            sendUsernameToServer();

        } catch (ServerConnectionErrorException e) {
            System.out.println("Error connecting to server:" + e);
            return false;
        } catch (IOStreamCreationException e) {
            System.out.println("error creating new Input/output Streams: " + e);
            return false;
        } catch (MessageSendingException e) {
            System.out.println("error thrown when user sends login name : " + e);
            disconnect();
            return false;
        }

        return true;
    }

    private void sendUsernameToServer() throws MessageSendingException {
        try {
            outputStream.writeObject(username);
        } catch (IOException eIO) {
            throw new MessageSendingException();
        }
    }

    private void createInputOutStreams() throws IOStreamCreationException {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new IOStreamCreationException();
        }
    }

    private void connectToServer() throws ServerConnectionErrorException {
        try {
            socket = new Socket(server, port);
            String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
            System.out.println(msg);
        } catch (IOException e) {
            throw new ServerConnectionErrorException();
        }
    }


    void sendMessage(ChatMessage msg) {
        try {
            outputStream.writeObject(msg);
        } catch (IOException e) {
            System.out.println("Exception writing to server: " + e);
        }
    }

    private void disconnect() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.println("Error closing the socket and streams" + e);
        }

    }

    private static boolean processInputMessage(WarriorClient client, String userInputMessage) {
        if (userInputMessage.equalsIgnoreCase(MessageType.LOGOUT.getShortValue())) {

            client.sendMessage(new ChatMessage(MessageType.LOGOUT, ""));
            return true;

        } else if (userInputMessage.contains(MessageType.LOAD_WARRIOR.getShortValue())) {

            loadWarrior(client, userInputMessage);

        } else if (userInputMessage.contains(MessageType.HELP.getShortValue())) {

            System.out.println(DisplayUtil.getHelpMessage());

        } else if (userInputMessage.contains(MessageType.WHOISIN.getShortValue())) {

            processWhoIsInCommand(client, userInputMessage);

        } else if (userInputMessage.contains(MessageType.ATTACK.getShortValue())) {

            processAttackCommand(client, userInputMessage);

        } else {
            client.sendMessage(new ChatMessage(MessageType.MESSAGE, userInputMessage));
        }
        return false;
    }


    private static ConnectionInformation initConnectionInformation(String[] args) {

        ConnectionInformation connectionInformation = new ConnectionInformation();
        switch (args.length) {
            // > javac Client username portNumber serverAddr
            case 3:
                connectionInformation.setServerAddress(args[2]);
                // > javac Client username portNumber
            case 2:
                try {
                    connectionInformation.setPortNumber(Integer.parseInt(args[1]));
                } catch (Exception e) {
                    DisplayUtil.displayInvalidPortNumberMessage();
                    return null;
                }
                // > javac Client username
            case 1:
                connectionInformation.setUserName(args[0]);
                // > java Client
            case 0:
                break;
            // invalid number of arguments
            default:
                DisplayUtil.displayInvalidArgument();
                return null;
        }

        return connectionInformation;
    }


    private static void processWhoIsInCommand(WarriorClient client, String userInputMessage) {
        String warriorName = extractValue(userInputMessage);

        client.sendMessage(new ChatMessage(MessageType.WHOISIN, warriorName));
    }


    private static void processAttackCommand(WarriorClient client, String userInputMessage) {

        if (warrior == null) {
            DisplayUtil.displayWarriorNotFound();
            return;
        }

        AttackMessage attackMessage = extractAttackMessage(userInputMessage);

        if (attackMessage != null) {

            if (isAttackMessageValid(client, attackMessage)) {
                client.sendMessage(new ChatMessage(MessageType.ATTACK, attackMessage));
            }

        } else {
            DisplayUtil.displayInvalidUseOfAttackCommand();
        }
    }


    private static boolean isAttackMessageValid(WarriorClient client, AttackMessage attackMessage) {
        if (StringUtils.equals(attackMessage.getPlayerName(), client.username)) {

            DisplayUtil.displayCannotAttackSelfError();
            return false;

        } else if (!warrior.isAttackAvailable(attackMessage.getAttack())) {

            DisplayUtil.displayAttackNotAvailable();
            return false;

        }
        return true;
    }


    private static void loadWarrior(WarriorClient client, String message) {
        String path = extractValue(message);

        if (StringUtils.isNotBlank(path)) {
            try {
                warrior = XmlUtil.readWarriorFromFile(path);

                DisplayUtil.displayEvent(String.format("warrior [%s] loaded.", warrior));

                client.sendMessage(new ChatMessage(MessageType.LOAD_WARRIOR, "", warrior));
            } catch (IOException e) {
                DisplayUtil.displayEvent("error loading warrior. please check file.");
            }
        } else {
            DisplayUtil.displayEvent("You need to specify the fully qualified path of the warrior.");
        }

    }

    private static AttackMessage extractAttackMessage(String message) {
        String[] messageValue = message.split(" ");

        if (messageValue.length > 3) {
            return new AttackMessage(messageValue[1], messageValue[2], messageValue[3]);
        } else {
            return null;
        }


    }

    private static String extractValue(String message) {
        String[] messageValue = message.split(" ");

        if (messageValue.length > 1) {
            System.out.println(messageValue[1]);
            return messageValue[1];
        } else {
            return "";
        }
    }


    class ListenFromServer extends Thread {

        public void run() {
            while (true) {
                try {
                    ChatMessage chatMessage = (ChatMessage) inputStream.readObject();

                    System.out.println(chatMessage.getMessage());
                    System.out.print("> ");

                } catch (Exception e) {
                    System.out.println("Server has close the connection: " + e);
                    break;
                }
            }
        }
    }
}

