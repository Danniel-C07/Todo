package edu.programacion.models;

import java.sql.Timestamp;

public class User {
    private int idUser;
    private String username;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;

    public User() {
    }

    // Constructor para registrar un nuevo usuario (sin ID ni fecha aún)
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Constructor completo (para cuando traemos el usuario desde MySQL)
    public User(int idUser, String username, String email, String passwordHash, Timestamp createdAt) {
        this.idUser = idUser;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{id=" + idUser + ", username='" + username + "', email='" + email + "'}";
    }
}