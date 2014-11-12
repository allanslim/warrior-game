package com.ahujalimtamayo.project.common;

public enum MessageType {
    WHOISIN("\\who"),
    MESSAGE("\\msg"),
    LOGOUT("\\logout"),
    LOAD_WARRIOR("\\lw"),
    WARRIOR_LOADED("warrior-loaded"),
    HELP("\\help"),
    ATTACK("\\attack"),
    STATISTIC("\\stat"),
    ATTACK_NOTIFY("\\attack-notify"),
    DEFEND_NOTIFY("\\defend-notify"),
    DEFEND("\\defend"),
    WARRIOR_DEATH_NOTIFY("warrior-death-notify");

    private String shortValue;

    MessageType(String shortValue) {
        this.shortValue = shortValue;
    }

    MessageType(String shortValue, int val) {

    }

    public String getShortValue() { return shortValue; }
}
