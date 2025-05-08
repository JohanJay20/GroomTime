package com.example.groomtime.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String role; // "admin" or "user"
    private String photoUrl;

    // Default constructor for Firebase
    public User() {}

    public User(String id, String name, String email, String role, String photoUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.photoUrl = photoUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
} 