package com.ahujalimtamayo.project.common;

public enum MessageType {
    WHOISIN("\\who"), MESSAGE("\\msg"), LOGOUT("\\logout"), LOAD_WARRIOR("\\lw"), HELP("\\help");

    private String shortValue;

    MessageType(String shortValue) {
        this.shortValue = shortValue;
    }

    public String getShortValue() { return shortValue; }
}
