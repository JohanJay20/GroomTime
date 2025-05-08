package com.example.groomtime.models;

import java.util.Date;

public class Appointment {
    private String id;
    private String userId;
    private String userName;
    private String serviceType;
    private Date appointmentDate;
    private String status; // "pending", "confirmed", "completed", "cancelled"
    private String notes;

    // Default constructor for Firebase
    public Appointment() {}

    public Appointment(String id, String userId, String userName, String serviceType, 
                      Date appointmentDate, String status, String notes) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.serviceType = serviceType;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 