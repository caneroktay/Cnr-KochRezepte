package com.cnr.kochrezepte.dao;

import com.cnr.kochrezepte.model.Zutat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZutatDAO {

    private Zutat mapRow(ResultSet rs) throws SQLException {
        return new Zutat(
                rs.getInt("ID"),
                rs.getInt("RezeptID"),
                rs.getString("Name"),
                rs.getDouble("Menge"),
                rs.getString("Einheit")
        );
    }

    public List<Zutat> findByRezeptId(int rezeptId) throws SQLException {
        List<Zutat> list = new ArrayList<>();
        String sql = "SELECT * FROM Zutat WHERE RezeptID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rezeptId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }
    public void insert(Zutat zutat) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            insert(conn, zutat);
        }
    }

    public void insert(Connection conn, Zutat zutat) throws SQLException {
        String sql = "INSERT INTO Zutat (RezeptID, Name, Menge, Einheit) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, zutat.getRezeptId());
            stmt.setString(2, zutat.getName());
            stmt.setDouble(3, zutat.getMenge());
            stmt.setString(4, zutat.getEinheit());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    zutat.setId(keys.getInt(1));
                }
            }
        }
    }

    public void update(Zutat zutat) throws SQLException {
        String sql = "UPDATE Zutat SET Name = ?, Menge = ?, Einheit = ? WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, zutat.getName());
            stmt.setDouble(2, zutat.getMenge());
            stmt.setString(3, zutat.getEinheit());
            stmt.setInt(4, zutat.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Zutat WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Wird i.d.R. nicht manuell gebraucht, da ON DELETE CASCADE in der DB
     * das automatisch erledigt (siehe Schema). Trotzdem hier verfuegbar,
     * z.B. wenn ein Rezept in der GUI "geleert" werden soll ohne es zu loeschen.
     */
    public void deleteByRezeptId(int rezeptId) throws SQLException {
        String sql = "DELETE FROM Zutat WHERE RezeptID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rezeptId);
            stmt.executeUpdate();
        }
    }
}
