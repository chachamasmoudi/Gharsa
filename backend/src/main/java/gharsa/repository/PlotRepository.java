package gharsa.repository;

import gharsa.config.Database;
import gharsa.model.Plot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlotRepository {

    private static final String COLS = "id, user_id, name, area_ha, crop, location";

    private Plot map(ResultSet rs) throws SQLException {
        Object area = rs.getObject("area_ha");
        return new Plot(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("name"),
                area == null ? null : ((Number) area).doubleValue(),
                rs.getString("crop"),
                rs.getString("location"));
    }

    public List<Plot> findAllByUser(long userId) {
        String sql = "SELECT " + COLS + " FROM plots WHERE user_id = ? ORDER BY id";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Plot> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lecture des parcelles", e);
        }
    }

    public Optional<Plot> findByIdAndUser(long id, long userId) {
        String sql = "SELECT " + COLS + " FROM plots WHERE id = ? AND user_id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lecture de la parcelle", e);
        }
    }

    public Plot create(long userId, String name, Double areaHa, String crop, String location) {
        String sql = "INSERT INTO plots (user_id, name, area_ha, crop, location) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, name);
            if (areaHa == null) ps.setNull(3, Types.REAL); else ps.setDouble(3, areaHa);
            ps.setString(4, crop);
            ps.setString(5, location);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Plot(keys.getLong(1), userId, name, areaHa, crop, location);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création de la parcelle", e);
        }
    }

    public boolean update(long id, long userId, String name, Double areaHa, String crop, String location) {
        String sql = "UPDATE plots SET name = ?, area_ha = ?, crop = ?, location = ? WHERE id = ? AND user_id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            if (areaHa == null) ps.setNull(2, Types.REAL); else ps.setDouble(2, areaHa);
            ps.setString(3, crop);
            ps.setString(4, location);
            ps.setLong(5, id);
            ps.setLong(6, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur modification de la parcelle", e);
        }
    }

    public boolean delete(long id, long userId) {
        String sql = "DELETE FROM plots WHERE id = ? AND user_id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression de la parcelle", e);
        }
    }
}