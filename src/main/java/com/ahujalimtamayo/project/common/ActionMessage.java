package com.ahujalimtamayo.project.common;

import java.io.Serializable;

public class ActionMessage implements Serializable {
    private String warriorName;
    private String actionName;
    private int actionPoint;

    public ActionMessage( String warriorName, String actionName, int actionPoint) {
        this.warriorName = warriorName;
        this.actionName = actionName;
        this.actionPoint = actionPoint;
    }


    public String getWarriorName() { return warriorName; }

    public String getActionName() { return actionName; }

    public int getActionPoint() { return actionPoint; }

    @Override
    public String toString() {
        return "ActionMessage{" +
                "warriorName='" + warriorName + '\'' +
                ", actionName='" + actionName + '\'' +
                ", actionPoint=" + actionPoint +
                '}';
    }
}
