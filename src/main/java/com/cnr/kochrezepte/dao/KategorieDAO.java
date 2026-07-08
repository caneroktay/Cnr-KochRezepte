package com.cnr.kochrezepte.dao;

import com.cnr.kochrezepte.model.Kategorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategorieDAO {

    private Kategorie mapRow(ResultSet rs) throws SQLException {
        return new Kategorie(rs.getInt("ID"), rs.getString("Name"));
    }

    public List<Kategorie> findAll() throws SQLException {
        List<Kategorie> list = new ArrayList<>();
        String sql = "SELECT * FROM Kategorie ORDER BY Name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Kategorie findById(int id) throws SQLException {
        String sql = "SELECT * FROM Kategorie WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    /**
     * Fuegt eine neue Kategorie ein und traegt die generierte ID
     * direkt in das uebergebene Objekt ein.
     */
    public void insert(Kategorie kategorie) throws SQLException {
        String sql = "INSERT INTO Kategorie (Name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, kategorie.getName());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    kategorie.setId(keys.getInt(1));
                }
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Kategorie WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
