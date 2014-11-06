package com.ahujalimtamayo.project.server;


import com.ahujalimtamayo.project.common.ChatMessage;
import com.ahujalimtamayo.project.common.DisplayUtil;
import com.ahujalimtamayo.project.model.Warrior;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WarriorServer {

    public static final int DEFAULT_PORT = 1500;
    private static int uniqueIdPerConnection;

    private ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();

    private SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm:ss");

    private int port;

    private boolean keepGoing = true;

    public WarriorServer(int port) {
        this.port = port;
    }


    public static void main(String[] args) {

        int portNumber = DEFAULT_PORT;

        switch (args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java -jar WarriorServer [portNumber]");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: > java -jar WarriorServer [portNumber]");
                return;
        }

        WarriorServer server = new WarriorServer(portNumber);
        server.start();
    }


    public void start() {

        try {
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(port);

            while (keepGoing) {
                DisplayUtil.displayEvent("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();

                // if I was asked to stop
                if (!keepGoing)
                    break;

                ClientThread clientThread = new ClientThread(socket);
                clientThreads.add(clientThread);

                clientThread.start();
            }

            // I was asked to stop
            closeServerAndClientSockets(serverSocket);

        } catch (IOException e) {
            String msg = displayTime.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            DisplayUtil.displayEvent(msg);
        }
    }

    private void closeServerAndClientSockets(ServerSocket serverSocket) {
        try {
            serverSocket.close();

            for (int i = 0; i < clientThreads.size(); ++i) {
                ClientThread clientThread = clientThreads.get(i);
                clientThread.closeAllResource();
            }
        } catch (Exception e) {
            DisplayUtil.displayEvent("Exception closing the server and clients: " + e);
        }
    }


    private synchronized void broadcastMessageToAllClients(String message) {

        String time = displayTime.format(new Date());

        String timedMessage = time + " " + message + "\n";

        System.out.print(timedMessage);


        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for (int i = clientThreads.size(); --i >= 0; ) {
            ClientThread ct = clientThreads.get(i);
            // try to write to the Client if it fails removeClientFromTheList it from the list
            if (!ct.writeMsg(timedMessage)) {
                clientThreads.remove(i);
                DisplayUtil.displayEvent("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    // for a client who logoff using the LOGOUT message
    synchronized void removeClientFromTheList(int id) {

        for (int i = 0; i < clientThreads.size(); ++i) {

            ClientThread ct = clientThreads.get(i);
            // found it
            if (ct.threadId == id) {
                clientThreads.remove(i);
                return;
            }
        }
    }


    class ClientThread extends Thread {

        private Socket socket;

        private ObjectInputStream inputStream;

        private ObjectOutputStream outputStream;

        private int threadId;

        private String username;

        private ChatMessage chatMessage;

        private String dateInString;

        private Warrior warrior;


        ClientThread(Socket socket) {

            threadId = ++uniqueIdPerConnection;

            this.socket = socket;

            System.out.println("Thread trying to create Object Input/Output Streams");
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                username = (String) inputStream.readObject();
                DisplayUtil.displayEvent(username + " just connected.");

            } catch (Exception e) {
                DisplayUtil.displayEvent("Exception creating new Input/output Streams: " + e);
                return;
            }
            dateInString = new Date().toString();
        }


        public void run() {

            boolean keepGoing = true;

            while (keepGoing) {
                try {
                    chatMessage = (ChatMessage) inputStream.readObject();
                } catch (Exception e) {
                    DisplayUtil.displayEvent(username + " Exception reading Streams: " + e);
                    break;
                }

                switch (chatMessage.getMessageType()) {

                    case MESSAGE:
                        broadcastMessageToAllClients(username + ": " + chatMessage.getMessage());
                        break;
                    case LOGOUT:
                        DisplayUtil.displayEvent(username + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case LOAD_WARRIOR:
                        warrior = chatMessage.getWarrior();
                        broadcastMessageToAllClients(username + " loaded: " + warrior.getName());
                        break;
                    case WHOISIN:
                        displayWarriorInfo(chatMessage.getMessage());
                        break;
                }
            }


            // remove myself from the arrayList containing the list of the
            // connected Clients
            removeClientFromTheList(threadId);

            closeAllResource();

        }

        private void displayWarriorInfo(String warriorName) {
            writeMsg("\n========= List of the users connected at " + displayTime.format(new Date()) + " =========\n" );

            for (int i = 0; i < clientThreads.size(); i++) {
                ClientThread ct = clientThreads.get(i);

                if (ct.warrior != null) {

                    writeMsg((i + 1) + ") User:" + ct.username + " connected since " + ct.dateInString + " has Warrior: " + ct.warrior.getName() + "\n");

                    if (StringUtils.equals(ct.warrior.getName(), warriorName)) {
                        writeMsg(ct.warrior.toString());
                    }
                } else {
                    writeMsg((i + 1) + ") User:" + ct.username + " connected since " + ct.dateInString);
                }

            }
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

        private boolean writeMsg(String msg) {

            if (!socket.isConnected()) {
                closeAllResource();
                return false;
            }

            try {

                outputStream.writeObject(msg);

            } catch (IOException e) {
                // if an error occurs, do not abort just inform the user
                DisplayUtil.displayEvent("Error sending message to " + username);
                DisplayUtil.displayEvent(e.toString());
            }
            return true;
        }
    }
}
