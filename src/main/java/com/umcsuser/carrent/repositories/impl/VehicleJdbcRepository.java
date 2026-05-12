package com.umcsuser.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.services.JdbcConnectionManager;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VehicleJdbcRepository implements VehicleRepository {

    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, category, brand, model, year, plate, price, attributes FROM vehicle";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicles", e);
        }

        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT id, category, brand, model, year, plate, price, attributes FROM vehicle WHERE id LIKE "+id;
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return Optional.ofNullable(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicles", e);
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        boolean edit;
        if(vehicle == null) {
            throw new IllegalArgumentException("vehicle cannot be null");
        }
        Vehicle toSave = vehicle.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
            edit=false;
        } else {
            deleteById(vehicle.getId());
            edit=true;
        }
        if(edit) {
            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement("""
                         UPDATE vehicle
                         SET
                             category = ?,
                             brand = ?,
                             model = ?,
                             year = ?,
                             plate = ?,
                             price = ?,
                             attributes = ?::jsonb
                         WHERE id = ?""")) {
                stmt.setString(1, toSave.getCategory());
                stmt.setString(2, toSave.getBrand());
                stmt.setString(3, toSave.getModel());
                stmt.setInt(4, toSave.getYear());
                stmt.setString(5, toSave.getPlate());
                stmt.setDouble(6, toSave.getPrice());
                stmt.setString(7, gson.toJson(toSave.getAttributes()));
                stmt.setString(8, toSave.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while saving vehicle", e);
            }
        } else {
            String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, vehicle.getId());
                stmt.setString(2, vehicle.getCategory());
                stmt.setString(3, vehicle.getBrand());
                stmt.setString(4, vehicle.getModel());
                stmt.setInt(5, vehicle.getYear());
                stmt.setString(6, vehicle.getPlate());
                stmt.setDouble(7, vehicle.getPrice());
                stmt.setString(8, gson.toJson(vehicle.getAttributes()));
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while saving vehicle", e);
            }
        }
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting vehicle", e);
        }
    }
    private Vehicle mapRow(ResultSet rs) throws SQLException {
        String attrJson = rs.getString("attributes");
        Map<String, Object> attributes = gson.fromJson(attrJson, mapType);

        return Vehicle.builder()
                .id(rs.getString("id"))
                .category(rs.getString("category"))
                .brand(rs.getString("brand"))
                .model(rs.getString("model"))
                .year(rs.getInt("year"))
                .plate(rs.getString("plate"))
                .price(rs.getDouble("price"))
                .attributes(attributes != null ? attributes : new HashMap<>())
                .build();
    }
}
