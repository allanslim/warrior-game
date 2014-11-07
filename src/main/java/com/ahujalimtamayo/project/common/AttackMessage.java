package com.ahujalimtamayo.project.common;

public class AttackMessage {
    private String playerName;
    private String victimName;
    private String attack;

    public AttackMessage( String playerName, String victimName, String attack) {
        this.victimName = victimName;
        this.attack = attack;
        this.playerName = playerName;
    }

    public String getVictimName() { return victimName; }

    public String getAttack() { return attack; }

    public String getPlayerName() { return playerName; }
}
