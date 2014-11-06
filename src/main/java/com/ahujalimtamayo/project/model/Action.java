package com.ahujalimtamayo.project.model;

import java.io.Serializable;

public interface Action extends Serializable {

    public enum ActionType {ATTACK, DEFENSE}

    public ActionType getType();


}
