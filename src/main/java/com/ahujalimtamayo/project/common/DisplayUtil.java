package com.ahujalimtamayo.project.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayUtil {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final String ATTACK_COMMAND_HELP = "\\attack <user name> <warrior name> <attack> - to attack a warrior.\n";


    public static void displayEvent(String message) {
        String time = DATE_FORMAT.format(new Date());
        System.out.println(time + " " + message);
    }

    public static String getHelpMessage() {
        String separator = "\n--------------------------------------------\n";
        StringBuilder helpMessageBuilder  = new StringBuilder(separator);
        helpMessageBuilder.append("\\logout - to logout of the system.\n");
        helpMessageBuilder.append("\\help - to display all the commands.\n");
        helpMessageBuilder.append("\\lw <path> - to load your warrior from a file.\n");
        helpMessageBuilder.append("\\who - to display all warriors in the arena.\n");
        helpMessageBuilder.append("\\who <warrior> - to display the stats of the warrior.\n");
        helpMessageBuilder.append(ATTACK_COMMAND_HELP);
        helpMessageBuilder.append(separator);
        return helpMessageBuilder.toString();
    }

    public static void displayInvalidPortNumberMessage() {
        System.out.println("Invalid port number.");
        System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
    }

    public static void displayInvalidArgument() {
        System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
    }

    public static void displayInvalidUseOfAttackCommand() {
        System.out.println(ATTACK_COMMAND_HELP);
    }

    public static void displayCannotAttackSelfError() {
        System.out.println("Oops! You cannot attack yourself.\n");
    }

    public static void displayAttackNotAvailable() {
        System.out.println("Oops! Your warrior does not have this attack.\n");
    }

    public static void displayWarriorNotFound() {
        System.out.println("Oops! You don't have a  warrior. Please load your warrior first using \\lw command.\n");
    }

    public static void displayHelp() {
        System.out.println(getHelpMessage());
    }

    public static void displayBroadcastMessage(ChatMessage chatMessage) {
        String time = DATE_FORMAT.format(new Date());

        String timedMessage = time + " " + chatMessage.getMessage() + "\n";

        System.out.print(timedMessage);
    }
}
