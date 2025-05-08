package com.example.groomtime;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groomtime.models.Appointment;
import com.example.groomtime.services.AppointmentService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    private boolean isAdminView;
    private AppointmentService appointmentService;
    private SimpleDateFormat dateFormat;
    private DatabaseReference mDatabase;

    public AppointmentAdapter(List<Appointment> appointments, boolean isAdminView) {
        this.appointments = appointments != null ? appointments : new ArrayList<>();
        this.isAdminView = isAdminView;
        this.appointmentService = new AppointmentService();
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void updateAppointments(List<Appointment> newAppointments) {
        this.appointments = newAppointments != null ? newAppointments : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        try {
            Appointment appointment = appointments.get(position);
            if (appointment != null) {
                // Add logging to check view type
                Log.d("AppointmentAdapter", "isAdminView: " + isAdminView);
                Log.d("AppointmentAdapter", "Appointment status: " + appointment.getStatus());

                holder.serviceTypeText.setText(appointment.getServiceType() != null ? 
                    appointment.getServiceType() : "No service type");
                
                if (appointment.getDateTime() != null) {
                    holder.dateTimeText.setText(dateFormat.format(appointment.getDateTime()));
                } else {
                    holder.dateTimeText.setText("No date/time");
                }
                
                holder.statusText.setText("Status: " + (appointment.getStatus() != null ? 
                    appointment.getStatus() : "No status"));

                // Set card background color based on status
                int backgroundColor;
                switch (appointment.getStatus().toLowerCase()) {
                    case "approved":
                        backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.green);
                        holder.buttonContainer.setVisibility(View.GONE);
                        break;
                    case "rejected":
                        backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.red);
                        holder.buttonContainer.setVisibility(View.GONE);
                        break;
                    case "cancelled":
                        backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.red);
                        holder.buttonContainer.setVisibility(View.GONE);
                        break;
                    default:
                        backgroundColor = holder.itemView.getContext().getResources().getColor(android.R.color.white);
                        holder.buttonContainer.setVisibility(View.VISIBLE);
                        break;
                }
                ((MaterialCardView) holder.itemView).setCardBackgroundColor(backgroundColor);

                // Set up buttons based on view type
                if (isAdminView) {
                    Log.d("AppointmentAdapter", "Setting up admin view buttons");
                    // Admin view - show approve and reject buttons
                    holder.approveButton.setVisibility(View.VISIBLE);
                    holder.rejectButton.setVisibility(View.VISIBLE);
                    holder.cancelButton.setVisibility(View.GONE);

                    // Set click listeners for admin buttons
                    holder.approveButton.setOnClickListener(v -> {
                        Log.d("AppointmentAdapter", "Approve button clicked");
                        updateAppointmentStatus(appointment.getId(), "approved");
                    });

                    holder.rejectButton.setOnClickListener(v -> {
                        Log.d("AppointmentAdapter", "Reject button clicked");
                        updateAppointmentStatus(appointment.getId(), "rejected");
                    });
                } else {
                    Log.d("AppointmentAdapter", "Setting up user view buttons");
                    // User view - show only cancel button
                    holder.approveButton.setVisibility(View.GONE);
                    holder.rejectButton.setVisibility(View.GONE);
                    
                    // Only show cancel button if appointment is not already cancelled/rejected/approved
                    if (!"cancelled".equalsIgnoreCase(appointment.getStatus()) && 
                        !"rejected".equalsIgnoreCase(appointment.getStatus()) &&
                        !"approved".equalsIgnoreCase(appointment.getStatus())) {
                        holder.cancelButton.setVisibility(View.VISIBLE);
                        holder.cancelButton.setOnClickListener(v -> {
                            Log.d("AppointmentAdapter", "Cancel button clicked");
                            cancelAppointment(appointment, holder.itemView.getContext());
                        });
                    } else {
                        holder.cancelButton.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("AppointmentAdapter", "Error binding view holder: " + e.getMessage());
        }
    }

    private void updateAppointmentStatus(String appointmentId, String newStatus) {
        if (appointmentId == null) {
            Log.e("AppointmentAdapter", "Appointment ID is null");
            return;
        }

        mDatabase.child("appointments").child(appointmentId).child("status")
            .setValue(newStatus)
            .addOnSuccessListener(aVoid -> {
                // Status updated successfully
                notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Log.e("AppointmentAdapter", "Error updating status: " + e.getMessage());
            });
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    private void updateAppointmentStatus(Appointment appointment, Context context) {
        if (appointment == null || appointment.getId() == null) {
            Toast.makeText(context, "Invalid appointment", Toast.LENGTH_SHORT).show();
            return;
        }

        String newStatus = "confirmed"; // later, replace with dialog logic
        appointmentService.updateAppointmentStatus(appointment.getId(), newStatus, new AppointmentService.OnAppointmentCallback() {
            @Override
            public void onSuccess(Appointment updatedAppointment) {
                Toast.makeText(context, "Appointment status updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, "Error updating status: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelAppointment(Appointment appointment, Context context) {
        if (appointment == null || appointment.getId() == null) {
            Log.e("AppointmentAdapter", "Invalid appointment");
            return;
        }

        mDatabase.child("appointments").child(appointment.getId())
            .removeValue()
            .addOnSuccessListener(aVoid -> {
                // Remove from local list
                int position = appointments.indexOf(appointment);
                if (position != -1) {
                    appointments.remove(position);
                    notifyItemRemoved(position);
                }
                Toast.makeText(context, 
                    "Appointment cancelled successfully", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Log.e("AppointmentAdapter", "Error cancelling appointment: " + e.getMessage());
                Toast.makeText(context, 
                    "Error cancelling appointment", Toast.LENGTH_SHORT).show();
            });
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTypeText;
        TextView dateTimeText;
        TextView statusText;
        LinearLayout buttonContainer;
        MaterialButton approveButton;
        MaterialButton rejectButton;
        MaterialButton cancelButton;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTypeText = itemView.findViewById(R.id.serviceTypeText);
            dateTimeText = itemView.findViewById(R.id.dateTimeText);
            statusText = itemView.findViewById(R.id.statusText);
            buttonContainer = itemView.findViewById(R.id.buttonContainer);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
} 