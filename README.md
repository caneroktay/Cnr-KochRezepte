# CNR-Kochrezepte

---

### Ziel des Systems

Das Programm soll Benutzern ermöglichen, Kochrezepte einfach zu verwalten. Die Benutzer können neue Rezepte eingeben, speichern, bearbeiten, löschen und suchen. Die Anwendung soll eine klare und einfache Benutzeroberfläche haben und alle Daten in einer MySQL-Datenbank speichern.

* Das Programm soll stabil laufen und keine Daten verlieren.
* Die Bedienung soll intuitiv sein.
* Die Datenbank soll mehrere hundert Rezepte speichern können.
* Das System soll auf Windows, MacOS und Linux funktionieren.

---

### Technologie-Stack

| Komponente    | Technologie              | Version |
| ------------- | ------------------------ | ------- |
| Sprache       | Java                     | 21      |
| GUI-Framework | JavaFX (Controls + FXML) | 21.0.2  |
| Datenbank     | MySQL                    | 8.x     |
| JDBC-Treiber  | mysql-connector-j        | 8.3.0   |
| Build-Tool    | Maven                    | –      |
| Styling       | CSS (JavaFX-Stylesheet)  | –      |

## Datenbankschema

Drei normalisierte Tabellen (3NF), über Fremdschlüssel verbunden:

```
Kategorie (1) ──< (n) Rezept (1) ──< (n) Zutat
```

| Tabelle       | Wichtige Spalten                                                                                       | Constraints                         |
| ------------- | ------------------------------------------------------------------------------------------------------ | ----------------------------------- |
| `Kategorie` | ID (PK), Name                                                                                          | UNIQUE(Name)                        |
| `Rezept`    | ID (PK), Titel, Beschreibung, Zubereitung, KategorieID (FK), Zubereitungszeit, ErstelltAm, GeaendertAm | FK → Kategorie, ON DELETE SET NULL |
| `Zutat`     | ID (PK), RezeptID (FK), Name, Menge, Einheit                                                           | FK → Rezept, ON DELETE CASCADE     |

Details und SQL-Skript siehe `01_schema.sql`.


## Architektur

Die Anwendung folgt dem  **MVC-Muster (Model-View-Controller)** :

* **Model** (`model/`) – reine Datenklassen ohne Logik: `Rezept`, `Zutat`, `Kategorie`
* **View** (`resources/fxml/`, `resources/style.css`) – JavaFX-FXML-Layouts und zentrales Stylesheet
* **Controller** (`controller/`) – verarbeitet Benutzerinteraktionen, verbindet View und Model/DAO-Schicht
* **DAO-Schicht** (`dao/`) – kapselt sämtliche JDBC-/SQL-Zugriffe, trennt Datenbanklogik von der GUI

Zusätzlich gibt es eine **Utility-Schicht** (`util/`) mit wiederverwendbaren Hilfsklassen (`AlertUtil`, `MengeFormatter`), die keiner der klassischen MVC-Schichten direkt zugeordnet sind.


## Projektstruktur

```
src/main/java/com/cnr/kochrezepte/
├── Main.java              	Einstiegspunkt (JavaFX Application)
├── model/                 	Rezept, Zutat, Kategorie
├── dao/                   	DBConnection, RezeptDAO, ZutatDAO, KategorieDAO
├── controller/            	MainController, RezeptListController,
│                          	RezeptFormController, KategorieController
└── util/                  	AlertUtil, MengeFormatter

src/main/resources/
├── fxml/                  	main.fxml, rezept-liste.fxml,
│                          	rezept-form.fxml, kategorie.fxml
├── style.css              	zentrales Stylesheet (Dark Theme)
└── db/
    ├── db.properties.example
    └── db.properties      	(lokal, nicht versioniert)
```


## Überblick

Java-Desktop-Anwendung zur Verwaltung von Kochrezepten (Anlegen, Suchen,
Anzeigen, Bearbeiten, Löschen). Entwickelt im Rahmen der Fachinformatiker Anwendungsentwicklung (Lernfeld LF-ZQ19A).
