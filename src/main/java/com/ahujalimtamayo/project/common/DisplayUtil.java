package com.ahujalimtamayo.project.common;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayUtil {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final String ATTACK_COMMAND_HELP = "\\attack <warrior name to attack> <attack> - to attack a warrior.\n";
    public static final String DEFEND_COMMAND_HELP = "\\defend <warrior name to defend from> <defense> - to defend your warrior.\n";


    public static void displayEvent(String message) {
        String time = DATE_FORMAT.format(new Date());
        System.out.println(time + " " + message);
    }

    private static String getHelpMessage() {
        String separator = "\n--------------------------------------------\n";
        StringBuilder helpMessageBuilder  = new StringBuilder(separator);
        helpMessageBuilder.append("\\logout - to logout of the system.\n");
        helpMessageBuilder.append("\\help - to display all the commands.\n");
        helpMessageBuilder.append("\\stat - to display the stats of the warrior.\n");
        helpMessageBuilder.append("\\lw <path> - to load your warrior from a file.\n");
        helpMessageBuilder.append("\\who - to display all warriors in the arena.\n");
        helpMessageBuilder.append("\\who <warrior> - to display the stats of the warrior.\n");
        helpMessageBuilder.append(DEFEND_COMMAND_HELP);
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

    public static void displayInvalidUseOfDefendCommand() {
        System.out.println(DEFEND_COMMAND_HELP);
    }

    public static void displayCannotDoActionError(MessageType messageType) {

        String action = getActionCommand(messageType);

        System.out.println("Oops! You cannot " + action + " yourself.\n");
    }

    private static String getActionCommand(MessageType messageType) {
        return messageType == MessageType.ATTACK? "attack" : "defense";
    }

    public static void displayAttackNotAvailable(MessageType messageType) {

        String action = getActionCommand(messageType);

        System.out.println("Oops! Your warrior does not have this " + action +".\n");
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

    public static void displayPrompt(String promptName) {

        System.out.print(StringUtils.stripToEmpty(promptName) + "> ");
    }
}
