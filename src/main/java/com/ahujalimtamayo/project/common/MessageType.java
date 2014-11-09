package com.ahujalimtamayo.project.common;

public enum MessageType {
    WHOISIN("\\who"),
    MESSAGE("\\msg"),
    LOGOUT("\\logout"),
    LOAD_WARRIOR("\\lw"),
    HELP("\\help"),
    ATTACK("\\attack"),
    STATISTIC("\\stat"),
    DEFENSE("\\defense");

    private String shortValue;

    MessageType(String shortValue) {
        this.shortValue = shortValue;
    }

    MessageType(String shortValue, int val) {

    }

    public String getShortValue() { return shortValue; }
}
