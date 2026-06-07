package com.umcsuser.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.services.JdbcConnectionManager;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {

    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

    private final DataSource dataSource;

    public RentalJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.id, r.rent_date, r.return_date, u.id AS user_id, u.login AS user_login, u.password_hash AS user_password_hash, u.role AS user_role, v.id as vehicle_id, v.category AS vehicle_category, v.brand AS vehicle_brand, v.model AS vehicle_model, v.year AS vehicle_year, v.plate AS vehicle_plate, v.price AS vehicle_price, v.attributes AS vehicle_attributes FROM rental r JOIN users u ON r.user_id = u.id JOIN vehicle v ON r.vehicle_id = v.id";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
        String sql = "SELECT r.id, r.rent_date, r.return_date, u.id AS user_id, u.login AS user_login, u.password_hash AS user_password_hash, u.role AS user_role, v.id as vehicle_id, v.category AS vehicle_category, v.brand AS vehicle_brand, v.model AS vehicle_model, v.year AS vehicle_year, v.plate AS vehicle_plate, v.price AS vehicle_price, v.attributes AS vehicle_attributes FROM rental r JOIN users u ON r.user_id = u.id JOIN vehicle v ON r.vehicle_id = v.id WHERE r.id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
             ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return Optional.ofNullable(mapRow(rs));
            } else {
                return Optional.empty();
            }
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
            Connection connection = DataSourceUtils.getConnection(dataSource);
            try (PreparedStatement stmt = connection.prepareStatement("""
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
            Connection connection = DataSourceUtils.getConnection(dataSource);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, toSave.getId());
                stmt.setString(2, toSave.getUserId());
                stmt.setString(3, toSave.getVehicleId());
                stmt.setString(4, toSave.getRentDateTime());
                stmt.setString(5, toSave.getReturnDateTime());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while saving rental", e);
            }
        }
        return toSave;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT r.id, r.rent_date, r.return_date, u.id AS user_id, u.login AS user_login, u.password_hash AS user_password_hash, u.role AS user_role, v.id as vehicle_id, v.category AS vehicle_category, v.brand AS vehicle_brand, v.model AS vehicle_model, v.year AS vehicle_year, v.plate AS vehicle_plate, v.price AS vehicle_price, v.attributes AS vehicle_attributes FROM rental r JOIN users u ON r.user_id = u.id JOIN vehicle v ON r.vehicle_id = v.id WHERE r.vehicle_id LIKE ? AND r.return_date IS NULL";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, vehicleId);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    return Optional.ofNullable(mapRow(rs));
                } else {
                    return Optional.empty();
                }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }
    }
    private Rental mapRow(ResultSet rs) throws SQLException {
        String attrJson = rs.getString("vehicle_attributes");
        Map<String, Object> attributes = gson.fromJson(attrJson, mapType);
        User user = User.builder()
                .id(rs.getString("user_id"))
                .login(rs.getString("user_login"))
                .passwordHash(rs.getString("user_password_hash"))
                .role(Role.valueOf(rs.getString("user_role")))
                .build();
        Vehicle vehicle = Vehicle.builder()
                .id(rs.getString("vehicle_id"))
                .category(rs.getString("vehicle_category"))
                .brand(rs.getString("vehicle_brand"))
                .model(rs.getString("vehicle_model"))
                .year(rs.getInt("vehicle_year"))
                .plate(rs.getString("vehicle_plate"))
                .price(rs.getDouble("vehicle_price"))
                .attributes(attributes != null ? attributes : new HashMap<>())
                .build();
        return Rental.builder()
                .id(rs.getString("id"))
                .user(user)
                .vehicle(vehicle)
                .rentDateTime(rs.getString("rent_date"))
                .returnDateTime(rs.getString("return_date"))
                .build();
    }
}
