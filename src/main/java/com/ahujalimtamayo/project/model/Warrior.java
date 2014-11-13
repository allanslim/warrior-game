package com.ahujalimtamayo.project.model;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

public class Warrior implements Serializable {

    public static final String DISPLAY_SEPARATOR = "\n---------------------------\n";
    private String name;
    private String description;
    private HealthLevel healthLevel;
    private String placeOfOrigin;
    private List<Attack> attacks = Lists.newArrayList();
    private List<Defense> defenses = Lists.newArrayList();

    public Warrior() {
    }

    public Warrior(String name, String description, String placeOfOrigin) {
        this.name = name;
        this.description = description;
        healthLevel = new HealthLevel();
    }

    public Warrior(String name, String description, HealthLevel healthLevel, String placeOfOrigin) {
        this.name = name;
        this.description = description;
        this.healthLevel = healthLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HealthLevel getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(HealthLevel healthLevel) {
        this.healthLevel = healthLevel;
    }

    public String getPlaceOfOrigin() {
        return placeOfOrigin;
    }

    public void setPlaceOfOrigin(String placeOfOrigin) {
        this.placeOfOrigin = placeOfOrigin;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }

    public void setAttacks(List<Attack> attacks) {
        this.attacks = attacks;
    }

    public List<Defense> getDefenses() {
        return defenses;
    }

    public void setDefenses(List<Defense> defenses) {
        this.defenses = defenses;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getHeathPoints() { return  healthLevel.getCurrentHealth(); }

    public void reduceHealthPoints(int point) { healthLevel.reduceHealthBy(point); }

    public void  addHealthPoints(int point) { healthLevel.addHealthBy(point); }

    public boolean isAttackAvailable(String attackName) {
        for( Attack attack : attacks) {
            if(StringUtils.equals(attack.getName(), attackName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDefenseAvailable(String defenseName) {
        for( Defense attack : defenses) {
            if(StringUtils.equals(attack.getName(), defenseName)) {
                return true;
            }
        }
        return false;
    }

    public int findAttackPoint(String attackName) {
        for( Attack attack : attacks) {
            if(StringUtils.equals(attack.getName(), attackName)) {
                return attack.getActionPoints();
            }
        }
        return 0;
    }

    public int findDefensePoint(String defenseName) {
        for( Defense defense : defenses) {
            if(StringUtils.equals(defense.getName(), defenseName)) {
                return defense.getActionPoints();
            }
        }
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Warrior warrior = (Warrior) o;

        if (attacks != null ? !attacks.equals(warrior.attacks) : warrior.attacks != null) return false;
        if (defenses != null ? !defenses.equals(warrior.defenses) : warrior.defenses != null) return false;
        if (description != null ? !description.equals(warrior.description) : warrior.description != null) return false;
        if (healthLevel != null ? !healthLevel.equals(warrior.healthLevel) : warrior.healthLevel != null) return false;
        if (name != null ? !name.equals(warrior.name) : warrior.name != null) return false;
        if (placeOfOrigin != null ? !placeOfOrigin.equals(warrior.placeOfOrigin) : warrior.placeOfOrigin != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (healthLevel != null ? healthLevel.hashCode() : 0);
        result = 31 * result + (placeOfOrigin != null ? placeOfOrigin.hashCode() : 0);
        result = 31 * result + (attacks != null ? attacks.hashCode() : 0);
        result = 31 * result + (defenses != null ? defenses.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder warriorBuilder = new StringBuilder(DISPLAY_SEPARATOR);
        warriorBuilder.append("Name: ").append(name).append("\n");
        warriorBuilder.append("Description: ").append(description).append("\n");
        warriorBuilder.append("Health Level: ").append(healthLevel.getCurrentHealth()).append("\n");
        warriorBuilder.append("Place Of Origin: ").append(placeOfOrigin).append("\n");
        warriorBuilder.append("Attacks:\n").append(attacks).append("\n");
        warriorBuilder.append("Defenses:\n").append(defenses).append("\n");
        warriorBuilder.append(DISPLAY_SEPARATOR);
        return warriorBuilder.toString();
    }


    public boolean isDead() {
        return healthLevel.getCurrentHealth() <= 0;
    }
}
