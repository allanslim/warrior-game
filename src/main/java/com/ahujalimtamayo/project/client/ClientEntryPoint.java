package com.ahujalimtamayo.project.client;

import com.ahujalimtamayo.project.common.DisplayUtil;

import java.util.Scanner;

public class ClientEntryPoint {

    public static void main(String[] args) {

        ConnectionInformation connectionInformation = createConnectionInformation(args);

        if(connectionInformation != null ) {
            WarriorClient warriorClient = new WarriorClient(connectionInformation);

            if( warriorClient.initialize() ) {

                Scanner scan = new Scanner(System.in);

                DisplayUtil.displayHelp();

                while(true) {

                    DisplayUtil.displayPrompt(warriorClient.getUsername());

                    String userInputMessage = scan.nextLine();

                    if(warriorClient.processInputMessage(userInputMessage)) break;

                }

                warriorClient.disconnect();
                System.exit(0);

            }

        }

    }


    private static ConnectionInformation createConnectionInformation(String[] args) {

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
}
