package com.cnr.kochrezepte.dao;

import com.cnr.kochrezepte.model.Kategorie;
import com.cnr.kochrezepte.model.Rezept;
import com.cnr.kochrezepte.model.Zutat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RezeptDAO {

    private final ZutatDAO zutatDAO = new ZutatDAO();

    private static final String BASE_SELECT =
            "SELECT r.ID, r.Titel, r.Beschreibung, r.Zubereitung, r.Zubereitungszeit, " +
            "       r.ErstelltAm, r.GeaendertAm, k.ID AS KategorieID, k.Name AS KategorieName " +
            "FROM Rezept r LEFT JOIN Kategorie k ON r.KategorieID = k.ID";

    /**
     * Wandelt EINE Zeile des ResultSet in EIN Rezept-Objekt um.
     * Laedt die Zutatenliste per separatem Aufruf nach (siehe unten).
     */
    private Rezept mapRow(ResultSet rs) throws SQLException {
        Rezept r = new Rezept();
        r.setId(rs.getInt("ID"));
        r.setTitel(rs.getString("Titel"));
        r.setBeschreibung(rs.getString("Beschreibung"));
        r.setZubereitung(rs.getString("Zubereitung"));
        r.setZubereitungszeit(rs.getInt("Zubereitungszeit"));

        Timestamp erstellt = rs.getTimestamp("ErstelltAm");
        if (erstellt != null) {
            r.setErstelltAm(erstellt.toLocalDateTime());
        }
        Timestamp geaendert = rs.getTimestamp("GeaendertAm");
        if (geaendert != null) {
            r.setGeaendertAm(geaendert.toLocalDateTime());
        }

        int katId = rs.getInt("KategorieID");
        if (!rs.wasNull()) {
            r.setKategorie(new Kategorie(katId, rs.getString("KategorieName")));
        }
        return r;
    }

    public List<Rezept> findAll() throws SQLException {
        List<Rezept> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT)) {
            while (rs.next()) {
                Rezept r = mapRow(rs);
                r.setZutaten(zutatDAO.findByRezeptId(r.getId()));
                list.add(r);
            }
        }
        return list;
    }

    /**
     * Gibt genau EIN Rezept zurueck (oder null, wenn nicht gefunden).
     */
    public Rezept findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Rezept r = mapRow(rs);
                r.setZutaten(zutatDAO.findByRezeptId(r.getId()));
                return r;
            }
        }
    }

    /**
     * Sucht nach Titel, Kategorie ODER Zutat (siehe Pflichtenheft 2.3).
     */
    public List<Rezept> search(String keyword) throws SQLException {
        List<Rezept> list = new ArrayList<>();
        String sql = "SELECT DISTINCT r.ID FROM Rezept r " +
                     "LEFT JOIN Kategorie k ON r.KategorieID = k.ID " +
                     "LEFT JOIN Zutat z ON z.RezeptID = r.ID " +
                     "WHERE r.Titel LIKE ? OR k.Name LIKE ? OR z.Name LIKE ?";

        String like = "%" + keyword + "%";
        List<Integer> matchingIds = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matchingIds.add(rs.getInt("ID"));
                }
            }
        }

        for (int id : matchingIds) {
            list.add(findById(id));
        }
        return list;
    }

    /**
     * Legt ein neues Rezept inkl. seiner Zutaten an.
     * Traegt die generierte ID direkt in das uebergebene Objekt ein.
     */
    public void insert(Rezept rezept) throws SQLException {
        String sql = "INSERT INTO Rezept (Titel, Beschreibung, Zubereitung, KategorieID, Zubereitungszeit) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, rezept.getTitel());
                stmt.setString(2, rezept.getBeschreibung());
                stmt.setString(3, rezept.getZubereitung());
                if (rezept.getKategorie() != null) {
                    stmt.setInt(4, rezept.getKategorie().getId());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }
                stmt.setInt(5, rezept.getZubereitungszeit());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        rezept.setId(keys.getInt(1));
                    }
                }

                for (Zutat z : rezept.getZutaten()) {
                    z.setRezeptId(rezept.getId());
                    zutatDAO.insert(conn, z);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void update(Rezept rezept) throws SQLException {
        String sql = "UPDATE Rezept SET Titel = ?, Beschreibung = ?, Zubereitung = ?, " +
                     "KategorieID = ?, Zubereitungszeit = ? WHERE ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rezept.getTitel());
            stmt.setString(2, rezept.getBeschreibung());
            stmt.setString(3, rezept.getZubereitung());
            if (rezept.getKategorie() != null) {
                stmt.setInt(4, rezept.getKategorie().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, rezept.getZubereitungszeit());
            stmt.setInt(6, rezept.getId());
            stmt.executeUpdate();
        }
        // Hinweis: Zutaten-Updates (hinzufuegen/entfernen/aendern) werden im
        // Formular-Controller ueber zutatDAO.insert/update/delete gesteuert,
        // da dort bekannt ist, welche Zeilen sich tatsaechlich geaendert haben.
    }

    public void delete(int id) throws SQLException {
        // Zutaten werden durch ON DELETE CASCADE in der DB automatisch entfernt.
        String sql = "DELETE FROM Rezept WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
