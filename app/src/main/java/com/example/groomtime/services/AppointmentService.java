package com.example.groomtime.services;

import android.util.Log;
import com.example.groomtime.models.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentService {
    private static final String TAG = "AppointmentService";
    private DatabaseReference mDatabase;

    public AppointmentService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public interface OnAppointmentsCallback {
        void onSuccess(List<Appointment> appointments);
        void onError(String errorMessage);
    }

    public void getUserAppointments(String userId, OnAppointmentsCallback callback) {
        try {
            if (userId == null || userId.isEmpty()) {
                callback.onError("Invalid user ID");
                return;
            }

            if (mDatabase == null) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
            }

            mDatabase.child("appointments")
                    .orderByChild("userId")
                    .equalTo(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                List<Appointment> appointments = new ArrayList<>();
                                
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        try {
                                            Appointment appointment = snapshot.getValue(Appointment.class);
                                            if (appointment != null) {
                                                appointment.setId(snapshot.getKey());
                                                appointments.add(appointment);
                                            }
                                        } catch (Exception e) {
                                            Log.e("AppointmentService", "Error processing appointment: " + e.getMessage());
                                        }
                                    }
                                }
                                
                                callback.onSuccess(appointments);
                            } catch (Exception e) {
                                callback.onError("Error processing data: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.onError(databaseError.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onError("Error accessing database: " + e.getMessage());
        }
    }

    public void createAppointment(Appointment appointment, OnAppointmentCallback callback) {
        String appointmentId = mDatabase.child("appointments").push().getKey();
        if (appointmentId != null) {
            appointment.setId(appointmentId);
            mDatabase.child("appointments").child(appointmentId).setValue(appointment)
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
        mDatabase.child("appointments").child(appointmentId).child("status").setValue(newStatus)
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