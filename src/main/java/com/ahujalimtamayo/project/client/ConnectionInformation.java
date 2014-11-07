package com.ahujalimtamayo.project.client;

import java.util.Random;

public class ConnectionInformation {

    private static final Random RANDOM = new Random();
    private int portNumber;
    private String serverAddress;
    private String userName;

    public ConnectionInformation() {
        portNumber = 1500;
        serverAddress = "localhost";
        userName = "Player" + RANDOM.nextInt(1000);
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
