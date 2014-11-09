package com.ahujalimtamayo.project.server;

public class ServerEntryPoint {

    public static final int DEFAULT_PORT = 1500;

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
        server.execute();
    }
}
