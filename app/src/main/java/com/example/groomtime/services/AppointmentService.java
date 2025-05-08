package com.example.groomtime.services;

import android.util.Log;
import com.example.groomtime.models.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;

public class AppointmentService {
    private static final String TAG = "AppointmentService";
    private final DatabaseReference appointmentsRef;

    public AppointmentService() {
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
    }

    public void createAppointment(Appointment appointment, OnAppointmentCallback callback) {
        String appointmentId = appointmentsRef.push().getKey();
        if (appointmentId != null) {
            appointment.setId(appointmentId);
            appointmentsRef.child(appointmentId).setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Appointment created successfully");
                    callback.onSuccess(appointment);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating appointment: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
        } else {
            callback.onError("Failed to generate appointment ID");
        }
    }

    public void updateAppointmentStatus(String appointmentId, String newStatus, OnAppointmentCallback callback) {
        appointmentsRef.child(appointmentId).child("status").setValue(newStatus)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Appointment status updated successfully");
                callback.onSuccess(null);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating appointment status: " + e.getMessage());
                callback.onError(e.getMessage());
            });
    }

    public interface OnAppointmentCallback {
        void onSuccess(Appointment appointment);
        void onError(String errorMessage);
    }
} 