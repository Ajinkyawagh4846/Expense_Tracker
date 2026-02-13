package com.expensetracker.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User model represents a registered user of the expense tracker.
 */
public class User implements Serializable {

    private int id;
    private String username;
    private String email;
    /**
     * Stored as SHA-256 hash string.
     */
    private String password;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(int id, String username, String email, String password, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

