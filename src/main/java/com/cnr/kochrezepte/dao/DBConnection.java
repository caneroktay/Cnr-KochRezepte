package com.cnr.kochrezepte.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


// ======================================================================
// Erstellt JDBC-Connections auf Basis von db/db.properties.
// --------------------
// Bewusst KEIN Singleton: jeder DAO-Aufruf oeffnet ueber getConnection()
// eine neue Connection und schliesst sie per try-with-resources wieder
// (siehe RezeptDAO, ZutatDAO, KategorieDAO). Fuer eine Desktop-Anwendung
// mit wenigen hundert Datensaetzen ist das einfach, sicher (keine
// "Connection ist bereits geschlossen"-Fehler) und ausreichend performant.
// ======================================================================

public final class DBConnection {

    private DBConnection() {
        // Utility-Klasse, keine Instanzierung erlaubt
    }

    public static Connection getConnection() throws SQLException {
        Properties props = loadProperties();
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }

    private static Properties loadProperties() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("db/db.properties")) {
            if (in == null) {
                throw new SQLException("db.properties nicht gefunden im Classpath (src/main/resources/db/)");
            }
            props.load(in);
        } catch (IOException e) {
            throw new SQLException("Fehler beim Lesen von db.properties", e);
        }
        return props;
    }
}
