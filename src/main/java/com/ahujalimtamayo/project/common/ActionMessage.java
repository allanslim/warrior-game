package com.ahujalimtamayo.project.common;

import java.io.Serializable;

public class ActionMessage implements Serializable {
    private String playerName;
    private String warriorName;
    private String actionName;
    private int actionPoint;

    public ActionMessage(String playerName, String warriorName, String actionName, int actionPoint) {
        this.warriorName = warriorName;
        this.actionName = actionName;
        this.playerName = playerName;
        this.actionPoint = actionPoint;
    }

    public String getWarriorName() { return warriorName; }

    public String getActionName() { return actionName; }

    public String getPlayerName() { return playerName; }

    public int getActionPoint() { return actionPoint; }
}
