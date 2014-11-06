package com.ahujalimtamayo.project.model;

import java.io.Serializable;

public class HealthLevel implements Serializable {

    private static final int MAXIMUM_HEALTH = 100;

    private int currentHealth;

    public HealthLevel() {
        currentHealth = MAXIMUM_HEALTH;
    }

    public void reduceHealthBy(int point) {
        validatePointValueNotMoreThanMaximum(point);

        if(currentHealth < point) {
            currentHealth = 0;
        }

        currentHealth = currentHealth - point;
    }

    private void validatePointValueNotMoreThanMaximum(int point) {
        if(point >= MAXIMUM_HEALTH) {
            throw new IllegalArgumentException("reduce point cannot be greater than or equal to MAXIMUM HEALTH");
        }
    }

    public int getCurrentHealth() { return currentHealth; }

    public void addHealthBy(int point) {
        validatePointValueNotMoreThanMaximum(point);

        currentHealth = currentHealth + point;

        if(currentHealth > MAXIMUM_HEALTH) {
            currentHealth = MAXIMUM_HEALTH;
        }
    }

    @Override
    public String toString() {
        return "HealthLevel{" +
                "currentHealth=" + currentHealth +
                '}';
    }
}
