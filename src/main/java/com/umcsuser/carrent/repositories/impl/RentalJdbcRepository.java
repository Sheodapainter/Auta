package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.services.JdbcConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalJdbcRepository implements RentalRepository {
    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT id, user_id, vehicle_id, rent_date, return_date FROM rental";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT id, user_id, vehicle_id, rent_date, return_date FROM rental WHERE id LIKE "+id;
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return Optional.ofNullable(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }
    }

    @Override
    public Rental save(Rental rental) {
        boolean save;
        if(rental == null) {
            throw new IllegalArgumentException("rental cannot be null");
        }
        Rental toSave = rental.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
            save=false;
        } else {
            save=true;
        }
        if (save) {
            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement("""
                         UPDATE rental
                         SET
                            user_id = ?,
                            vehicle_id = ?,
                            rent_date = ?,
                            return_date = ?
                         WHERE id = ?
                         """)) {
                stmt.setString(1, toSave.getUserId());
                stmt.setString(2, toSave.getVehicleId());
                stmt.setString(3, toSave.getRentDateTime());
                stmt.setString(4, toSave.getReturnDateTime());
                stmt.setString(5, toSave.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while saving rental", e);
            }
        } else {
            String sql = "INSERT INTO rental (id, user_id, vehicle_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, rental.getId());
                stmt.setString(2, rental.getUserId());
                stmt.setString(3, rental.getVehicleId());
                stmt.setString(4, rental.getRentDateTime());
                stmt.setString(5, rental.getReturnDateTime());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while saving rental", e);
            }
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT id, user_id, vehicle_id, rent_date, return_date FROM rental WHERE vehicle_id LIKE "+vehicleId;
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return Optional.ofNullable(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }
    }
    private Rental mapRow(ResultSet rs) throws SQLException {
        return Rental.builder()
                .id(rs.getString("id"))
                .userId(rs.getString("user_id"))
                .vehicleId(rs.getString("vehicle_id"))
                .rentDateTime(rs.getString("rent_date"))
                .returnDateTime(rs.getString("return_date"))
                .build();
    }
}
