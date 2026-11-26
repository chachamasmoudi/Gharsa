package gharsa.repository;

import gharsa.config.Database;
import gharsa.model.Plot;
import gharsa.model.PlotData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlotRepository {

    private static final String COLS =
            "id, user_id, name, latitude, longitude, area, crop_type, soil_type, planting_date, growth_stage";

    private static Double dbl(ResultSet rs, String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : v;
    }

    private static void setDbl(PreparedStatement ps, int i, Double v) throws SQLException {
        if (v == null) ps.setNull(i, Types.REAL); else ps.setDouble(i, v);
    }

    private Plot map(ResultSet rs) throws SQLException {
        return new Plot(rs.getLong("id"), rs.getLong("user_id"), rs.getString("name"),
                dbl(rs, "latitude"), dbl(rs, "longitude"), dbl(rs, "area"),
                rs.getString("crop_type"), rs.getString("soil_type"),
                rs.getString("planting_date"), rs.getString("growth_stage"));
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

    public Plot create(long userId, PlotData d) {
        String sql = "INSERT INTO plots (user_id, name, latitude, longitude, area, crop_type, soil_type, "
                + "planting_date, growth_stage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, d.name());
            setDbl(ps, 3, d.latitude());
            setDbl(ps, 4, d.longitude());
            setDbl(ps, 5, d.area());
            ps.setString(6, d.cropType());
            ps.setString(7, d.soilType());
            ps.setString(8, d.plantingDate());
            ps.setString(9, d.growthStage());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Plot(keys.getLong(1), userId, d.name(), d.latitude(), d.longitude(), d.area(),
                        d.cropType(), d.soilType(), d.plantingDate(), d.growthStage());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création de la parcelle", e);
        }
    }

    public boolean update(long id, long userId, PlotData d) {
        String sql = "UPDATE plots SET name = ?, latitude = ?, longitude = ?, area = ?, crop_type = ?, "
                + "soil_type = ?, planting_date = ?, growth_stage = ? WHERE id = ? AND user_id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, d.name());
            setDbl(ps, 2, d.latitude());
            setDbl(ps, 3, d.longitude());
            setDbl(ps, 4, d.area());
            ps.setString(5, d.cropType());
            ps.setString(6, d.soilType());
            ps.setString(7, d.plantingDate());
            ps.setString(8, d.growthStage());
            ps.setLong(9, id);
            ps.setLong(10, userId);
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