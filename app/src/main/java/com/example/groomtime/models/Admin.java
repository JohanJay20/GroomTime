package com.example.groomtime.models;

public class Admin {
    private String uid;
    private String email;
    private String name;
    private String role;
    private boolean isActive;

    public Admin() {
        // Required empty constructor for Firebase
    }

    public Admin(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.role = "admin";
        this.isActive = true;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
} 