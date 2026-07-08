# Pflichtenheft – CNR-Kochrezepte-Verwaltungssystem

### Version 2.0 (überarbeitet)

## 1. Ziel des Systems

Das Programm soll Benutzern ermöglichen, Kochrezepte einfach zu verwalten. Die Benutzer können neue Rezepte eingeben, speichern, bearbeiten, löschen und suchen. Die Anwendung soll eine klare und einfache Benutzeroberfläche haben und alle Daten in einer MySQL-Datenbank speichern.

## 2. Hauptfunktionen

#### 2.1 Rezepte eingeben

Der Benutzer kann ein neues Rezept mit Titel, Beschreibung, Zubereitungsschritten, Zutaten, Kategorie und Zubereitungszeit erfassen.

#### 2.2 Rezepte speichern

Alle Rezepte werden dauerhaft in einer MySQL-Datenbank gespeichert. Die Speicherung erfolgt über JDBC.

#### 2.3 Rezepte suchen

Der Benutzer kann Rezepte nach Titel, Kategorie oder Zutaten suchen. Die Suchergebnisse werden in einer Liste angezeigt.

#### 2.4 Rezepte anzeigen

Ein ausgewähltes Rezept wird vollständig angezeigt: Titel, Beschreibung, Zutatenliste (mit Menge und Einheit), Zubereitungsschritte, Kategorie und Zeitangabe.

#### 2.5 Rezepte bearbeiten

Der Benutzer kann bestehende Rezepte ändern und die Änderungen erneut speichern.

#### 2.6 Rezepte löschen

Der Benutzer kann ein Rezept aus der Datenbank entfernen. Die zugehörigen Zutaten werden automatisch mitgelöscht (Kaskadenlöschung).

#### 2.7 Kategorien verwalten

Der Benutzer kann Kategorien anlegen und Rezepten zuordnen. Wird eine Kategorie gelöscht, bleiben die zugehörigen Rezepte erhalten (Kategoriefeld wird auf „ohne Kategorie" gesetzt).

## 3. Benutzeroberfläche (GUI)

- Die Anwendung wird als Java-Desktop-Programm mit **JavaFX** entwickelt (FXML + Scene Builder).
- Die GUI besteht aus:
  - Hauptfenster mit Navigation
  - Rezept-Eingabeformular
  - Rezept-Liste (filterbar nach Kategorie)
  - Rezept-Detailansicht
  - Kategorie-Verwaltung (einfache Liste zum Anlegen/Löschen)
- Die Oberfläche soll übersichtlich und einfach bedienbar sein.

## 4. Datenbankanforderungen

- Datenbank: MySQL
- Tabellen:

**Kategorie**

| Spalte | Typ                           | Beschreibung       |
| ------ | ----------------------------- | ------------------ |
| ID     | INT, PK, AUTO_INCREMENT       | Eindeutige ID      |
| Name   | VARCHAR(50), NOT NULL, UNIQUE | Name der Kategorie |

**Rezept**

| Spalte           | Typ                                    | Beschreibung                          |
| ---------------- | -------------------------------------- | ------------------------------------- |
| ID               | INT, PK, AUTO_INCREMENT                | Eindeutige ID                         |
| Titel            | VARCHAR(100), NOT NULL                 | Titel des Rezepts                     |
| Beschreibung     | TEXT                                   | Kurzbeschreibung                      |
| Zubereitung      | TEXT                                   | Zubereitungsschritte (als Fließtext) |
| KategorieID      | INT, FK → Kategorie(ID), NULL erlaubt | Zugeordnete Kategorie                 |
| Zubereitungszeit | INT                                    | Zeit in Minuten                       |
| ErstelltAm       | DATETIME, DEFAULT CURRENT_TIMESTAMP    | Erstellungsdatum                      |
| GeaendertAm      | DATETIME, ON UPDATE CURRENT_TIMESTAMP  | Datum der letzten Änderung           |

**Zutat**

| Spalte   | Typ                                                | Beschreibung                      |
| -------- | -------------------------------------------------- | --------------------------------- |
| ID       | INT, PK, AUTO_INCREMENT                            | Eindeutige ID                     |
| RezeptID | INT, FK → Rezept(ID), NOT NULL, ON DELETE CASCADE | Zugehöriges Rezept               |
| Name     | VARCHAR(100), NOT NULL                             | Name der Zutat                    |
| Menge    | DECIMAL(6,2)                                       | Mengenangabe                      |
| Einheit  | VARCHAR(20)                                        | Maßeinheit (z. B. g, ml, Stück) |

- Tabellen sind über Fremdschlüssel verbunden (Kategorie 1–n Rezept, Rezept 1–n Zutat).
- CRUD-Operationen müssen für alle drei Tabellen vollständig unterstützt werden.

### SQL-Skript

```sql
CREATE TABLE Kategorie (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Rezept (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Titel VARCHAR(100) NOT NULL,
    Beschreibung TEXT,
    Zubereitung TEXT,
    KategorieID INT,
    Zubereitungszeit INT,
    ErstelltAm DATETIME DEFAULT CURRENT_TIMESTAMP,
    GeaendertAm DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (KategorieID) REFERENCES Kategorie(ID) ON DELETE SET NULL
);

CREATE TABLE Zutat (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    RezeptID INT NOT NULL,
    Name VARCHAR(100) NOT NULL,
    Menge DECIMAL(6,2),
    Einheit VARCHAR(20),
    FOREIGN KEY (RezeptID) REFERENCES Rezept(ID) ON DELETE CASCADE
);
```

## 5. Technische Anforderungen

- Programmiersprache: Java
- GUI-Framework: **JavaFX** (FXML)
- Datenbank: MySQL
- Verbindung: JDBC
- Architektur: MVC (Model-View-Controller)
  - Model: `Rezept`, `Zutat`, `Kategorie` (POJOs)
  - DAO-Schicht: `RezeptDAO`, `ZutatDAO`, `KategorieDAO`
  - Controller: JavaFX-Controller-Klassen
  - View: FXML-Dateien
- Fehlerbehandlung: `SQLException` wird abgefangen und als verständliche Fehlermeldung in der GUI angezeigt.
- Validierung: Pflichtfelder wie Titel dürfen nicht leer sein.

## 6. Nicht-Funktionale Anforderungen

- Das Programm soll stabil laufen und keine Daten verlieren.
- Die Bedienung soll intuitiv sein.
- Die Datenbank soll mehrere hundert Rezepte speichern können.
- Das System soll auf Windows, MacOS und Linux funktionieren.

## 7. Änderungshistorie

| Version | Änderung                                                                                                                                                                                                                                                                                                          |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1.0     | Ursprüngliches Pflichtenheft                                                                                                                                                                                                                                                                                      |
| 2.0     | GUI-Framework auf JavaFX festgelegt; Kategorie als eigene, normalisierte Tabelle mit Fremdschlüssel eingeführt; Zubereitungsschritte als Feld ergänzt (vorher inkonsistent, in 2.4 erwähnt aber im DB-Schema gefehlt); Zutat-Tabelle um Einheit erweitert; Zeitstempelfelder (ErstelltAm/GeaendertAm) ergänzt |
