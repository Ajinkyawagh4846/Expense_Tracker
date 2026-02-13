package com.expensetracker.dao;

import com.expensetracker.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Data access object for user-related operations.
 */
public class UserDAO {

    private static final String INSERT_USER_SQL =
            "INSERT INTO users (username, email, password, created_at) VALUES (?, ?, ?, NOW())";

    private static final String SELECT_BY_USERNAME_OR_EMAIL_SQL =
            "SELECT id, username, email, password, created_at FROM users WHERE username = ? OR email = ?";

    private static final String SELECT_BY_ID_SQL =
            "SELECT id, username, email, password, created_at FROM users WHERE id = ?";

    private static final String CHECK_USERNAME_SQL =
            "SELECT id FROM users WHERE username = ?";

    private static final String CHECK_EMAIL_SQL =
            "SELECT id FROM users WHERE email = ?";

    /**
     * Registers a new user in the database.
     *
     * @param user user object with username, email and hashed password
     * @return true if registration succeeded
     * @throws SQLException if database error occurs
     */
    public boolean registerUser(User user) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Authenticates user by username/email and password hash.
     *
     * @param usernameOrEmail username or email
     * @param passwordHash    hashed password
     * @return User if authenticated, otherwise null
     * @throws SQLException if database error occurs
     */
    public User authenticate(String usernameOrEmail, String passwordHash) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME_OR_EMAIL_SQL)) {
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (storedHash != null && storedHash.equals(passwordHash)) {
                        return mapRowToUser(rs);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds user by id.
     */
    public User findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Checks if a username already exists.
     */
    public boolean isUsernameExists(String username) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_USERNAME_SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks if an email already exists.
     */
    public boolean isEmailExists(String email) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_EMAIL_SQL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            user.setCreatedAt(createdTs.toLocalDateTime());
        } else {
            user.setCreatedAt(LocalDateTime.now());
        }
        return user;
    }
}

