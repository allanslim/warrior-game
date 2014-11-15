package com.ahujalimtamayo.project.server;

import com.ahujalimtamayo.project.common.DisplayUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WarriorServer {

    private static int UNIQUE_ID_PER_CONNECTION;

    private ArrayList<ServerThread> serverThreads = new ArrayList<ServerThread>();

    private SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm:ss");


    private int port;

    private boolean keepGoing = true;


    public WarriorServer(int port) {
        this.port = port;
    }

    public void execute() {

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (keepGoing) {
                DisplayUtil.displayEvent("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();

                // if I was asked to stop
                if (!keepGoing)
                    break;

                ServerThread serverThread = new ServerThread(socket, ++UNIQUE_ID_PER_CONNECTION, serverThreads);

                serverThreads.add(serverThread);

                serverThread.start();
            }

            // I was asked to stop
            closeServerAndClientSockets(serverSocket);

        } catch (Exception e) {
            String msg = displayTime.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            DisplayUtil.displayEvent(msg);
        }
    }

    private void closeServerAndClientSockets(ServerSocket serverSocket) {
        try {
            serverSocket.close();

            for (int i = 0; i < serverThreads.size(); ++i) {
                ServerThread serverThread = serverThreads.get(i);
                serverThread.closeAllResource();
            }
        } catch (Exception e) {
            DisplayUtil.displayEvent("Exception closing the server and clients: " + e);
        }
    }
}
