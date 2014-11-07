package com.ahujalimtamayo.project.common;

import java.io.Serializable;

public class AttackMessage implements Serializable {
    private String playerName;
    private String warriorName;
    private String attack;

    public AttackMessage( String playerName, String warriorName, String attack) {
        this.warriorName = warriorName;
        this.attack = attack;
        this.playerName = playerName;
    }

    public String getWarriorName() { return warriorName; }

    public String getAttack() { return attack; }

    public String getPlayerName() { return playerName; }
}
