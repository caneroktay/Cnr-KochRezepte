package com.cnr.kochrezepte.model;

import com.cnr.kochrezepte.util.MengeFormatter;


/**
 * Repraesentiert eine einzelne Zutat innerhalb eines Rezepts.
 */
public class Zutat {

    private int id;
    private int rezeptId;
    private String name;
    private double menge;
    private String einheit;

    public Zutat() {
    }

    public Zutat(String name, double menge, String einheit) {
        this.name = name;
        this.menge = menge;
        this.einheit = einheit;
    }

    public Zutat(int id, int rezeptId, String name, double menge, String einheit) {
        this.id = id;
        this.rezeptId = rezeptId;
        this.name = name;
        this.menge = menge;
        this.einheit = einheit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRezeptId() {
        return rezeptId;
    }

    public void setRezeptId(int rezeptId) {
        this.rezeptId = rezeptId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMenge() {
        return menge;
    }

    public void setMenge(double menge) {
        this.menge = menge;
    }

    public String getEinheit() {
        return einheit;
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }

    @Override
    public String toString() {
        return MengeFormatter.format(menge) + " " + einheit + " " + name;
    }
}
