package com.ahujalimtamayo.project.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayUtil {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");


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
        helpMessageBuilder.append(separator);
        return helpMessageBuilder.toString();
    }

}
