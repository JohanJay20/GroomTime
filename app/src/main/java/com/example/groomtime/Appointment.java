package com.example.groomtime;

public class Appointment {
    private String id;
    private String userId;
    private String serviceType;
    private String dateTime;
    private String status;

    // Default constructor required for Firebase
    public Appointment() {
    }

    // Constructor with parameters
    public Appointment(String id, String userId, String serviceType, String dateTime, String status) {
        this.id = id;
        this.userId = userId;
        this.serviceType = serviceType;
        this.dateTime = dateTime;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 