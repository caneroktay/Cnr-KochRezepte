package com.cnr.kochrezepte.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repraesentiert ein Kochrezept inklusive seiner Zutatenliste.
 */
public class Rezept {

    private int id;
    private String titel;
    private String beschreibung;
    private String zubereitung;
    private Kategorie kategorie;      // kann null sein (Rezept ohne Kategorie)
    private int zubereitungszeit;     // in Minuten
    private LocalDateTime erstelltAm;
    private LocalDateTime geaendertAm;
    private List<Zutat> zutaten = new ArrayList<>();

    public Rezept() {
    }

    public Rezept(String titel, String beschreibung, String zubereitung,
                   Kategorie kategorie, int zubereitungszeit) {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.zubereitung = zubereitung;
        this.kategorie = kategorie;
        this.zubereitungszeit = zubereitungszeit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getZubereitung() {
        return zubereitung;
    }

    public void setZubereitung(String zubereitung) {
        this.zubereitung = zubereitung;
    }

    public Kategorie getKategorie() {
        return kategorie;
    }

    public void setKategorie(Kategorie kategorie) {
        this.kategorie = kategorie;
    }

    public int getZubereitungszeit() {
        return zubereitungszeit;
    }

    public void setZubereitungszeit(int zubereitungszeit) {
        this.zubereitungszeit = zubereitungszeit;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public LocalDateTime getGeaendertAm() {
        return geaendertAm;
    }

    public void setGeaendertAm(LocalDateTime geaendertAm) {
        this.geaendertAm = geaendertAm;
    }

    public List<Zutat> getZutaten() {
        return zutaten;
    }

    public void setZutaten(List<Zutat> zutaten) {
        this.zutaten = zutaten;
    }

    public void addZutat(Zutat zutat) {
        zutat.setRezeptId(this.id);
        this.zutaten.add(zutat);
    }

    public void removeZutat(Zutat zutat) {
        this.zutaten.remove(zutat);
    }

    /**
     * Prueft die Pflichtfelder (siehe Pflichtenheft Punkt 5: Validierung).
     * Wird vom Controller vor dem Speichern aufgerufen.
     */
    public boolean isValid() {
        return titel != null && !titel.isBlank();
    }

    @Override
    public String toString() {
        return titel;
    }
}
