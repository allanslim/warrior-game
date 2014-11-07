package com.ahujalimtamayo.project.model;

public class Attack implements Action {

    private int actionPoints;
    private String name;
    private ActionType type;

    @Override
    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public void setActionPoints(int actionPoints) {
        this.actionPoints = actionPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder attackBuilder = new StringBuilder();
        attackBuilder.append("Name:").append(name).append(", damage points:").append(actionPoints).append("\n");
        return attackBuilder.toString();
    }
}
