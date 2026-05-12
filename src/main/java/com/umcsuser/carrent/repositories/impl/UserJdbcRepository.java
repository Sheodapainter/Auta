package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.services.JdbcConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserJdbcRepository implements UserRepository {
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, login, password_hash, role FROM users";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }

        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT id, login, password_hash, role FROM users WHERE id LIKE "+id;
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return Optional.ofNullable(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT id, login, password_hash, role FROM users WHERE login LIKE "+login;
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return Optional.ofNullable(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }
    }

    @Override
    public User save(User user) {
        boolean save;
        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        User toSave = user.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
            save=false;
        } else {
            save=true;
        }
        if (save) {
            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement("""
                         UPDATE users
                         SET
                            login = ?,
                            password_hash = ?,
                            role = ?
                         WHERE id = ?
                         """)) {
                stmt.setString(1, toSave.getLogin());
                stmt.setString(2, toSave.getPasswordHash());
                stmt.setString(3, String.valueOf(toSave.getRole()));
                stmt.setString(4, toSave.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while saving user", e);
            }
        } else {
            String sql = "INSERT INTO users (id, login, password_hash, role) VALUES (?, ?, ?, ?)";
            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, user.getId());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getPasswordHash());
                stmt.setString(4, String.valueOf(user.getRole()));
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while saving user", e);
            }
        }
        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting user", e);
        }
    }
    private User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getString("id"))
                .login(rs.getString("login"))
                .passwordHash(rs.getString("password_hash"))
                .role(Role.valueOf(rs.getString("role")))
                .build();
    }
}
