package com.example.groomtime.models;

import com.google.firebase.database.Exclude;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Appointment {
    private String id;
    private String userId;
    private String userName;
    private String serviceType;
    private Date dateTime;
    private String status; // "pending", "confirmed", "completed", "cancelled"
    private String notes;

    // Default constructor required for Firebase
    public Appointment() {
    }

    // Constructor with all parameters
    public Appointment(String id, String userId, String userName, String serviceType, 
                      Date dateTime, String status, String notes) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.serviceType = serviceType;
        this.dateTime = dateTime;
        this.status = status;
        this.notes = notes;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getServiceType() { return serviceType; }
    public Date getDateTime() { return dateTime; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }

    // Convert to Map for Firebase
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("userId", userId);
        result.put("userName", userName);
        result.put("serviceType", serviceType);
        result.put("dateTime", dateTime);
        result.put("status", status);
        result.put("notes", notes);
        return result;
    }
} 