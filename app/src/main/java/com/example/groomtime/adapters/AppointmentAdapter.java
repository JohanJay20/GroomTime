package com.example.groomtime.adapters;

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
import com.example.groomtime.R;
import com.example.groomtime.models.Appointment;
import com.example.groomtime.services.AppointmentService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private static final String TAG = "AppointmentAdapter";
    private List<Appointment> allAppointments;
    private List<Appointment> filteredAppointments;
    private boolean isAdminView;
    private String currentFilter;
    private SimpleDateFormat dateFormat;

    public AppointmentAdapter(List<Appointment> appointments, boolean isAdminView) {
        this.allAppointments = appointments != null ? appointments : new ArrayList<>();
        this.filteredAppointments = new ArrayList<>(this.allAppointments);
        this.isAdminView = isAdminView;
        this.currentFilter = null;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        Log.d(TAG, "Adapter initialized with " + this.allAppointments.size() + " appointments");
    }

    public void updateAppointments(List<Appointment> newAppointments) {
        Log.d(TAG, "Updating appointments. New size: " + (newAppointments != null ? newAppointments.size() : 0));
        this.allAppointments = newAppointments != null ? newAppointments : new ArrayList<>();
        applyFilter(currentFilter);
    }

    public void setFilter(String status) {
        Log.d(TAG, "Setting filter to: " + status);
        this.currentFilter = status;
        applyFilter(status);
    }

    private void applyFilter(String status) {
        Log.d(TAG, "Applying filter: " + status);
        filteredAppointments.clear();
        
        if (status == null) {
            // Show all appointments
            filteredAppointments.addAll(allAppointments);
            Log.d(TAG, "Showing all appointments: " + filteredAppointments.size());
        } else {
            // Filter by status
            for (Appointment appointment : allAppointments) {
                String appointmentStatus = appointment.getStatus();
                Log.d(TAG, "Checking appointment status: " + appointmentStatus + " against filter: " + status);
                
                if (appointmentStatus != null && appointmentStatus.equalsIgnoreCase(status)) {
                    filteredAppointments.add(appointment);
                    Log.d(TAG, "Added appointment to filtered list. Current size: " + filteredAppointments.size());
                }
            }
        }
        
        Log.d(TAG, "Final filtered appointments count: " + filteredAppointments.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = filteredAppointments.get(position);
        if (appointment != null) {
            Log.d(TAG, "Binding appointment: " + appointment.getServiceType() + " Status: " + appointment.getStatus());
            
            holder.serviceTypeText.setText(appointment.getServiceType());
            holder.dateTimeText.setText(dateFormat.format(appointment.getDateTime()));
            holder.statusText.setText("Status: " + appointment.getStatus());

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
                default:
                    backgroundColor = holder.itemView.getContext().getResources().getColor(android.R.color.white);
                    holder.buttonContainer.setVisibility(View.VISIBLE);
                    break;
            }
            ((MaterialCardView) holder.itemView).setCardBackgroundColor(backgroundColor);

            // Set up buttons based on view type
            if (isAdminView) {
                holder.approveButton.setVisibility(View.VISIBLE);
                holder.rejectButton.setVisibility(View.VISIBLE);
                holder.cancelButton.setVisibility(View.GONE);

                holder.approveButton.setOnClickListener(v -> {
                    updateAppointmentStatus(appointment.getId(), "approved");
                });

                holder.rejectButton.setOnClickListener(v -> {
                    updateAppointmentStatus(appointment.getId(), "rejected");
                });
            } else {
                holder.approveButton.setVisibility(View.GONE);
                holder.rejectButton.setVisibility(View.GONE);
                
                if (!"cancelled".equalsIgnoreCase(appointment.getStatus()) && 
                    !"rejected".equalsIgnoreCase(appointment.getStatus()) &&
                    !"approved".equalsIgnoreCase(appointment.getStatus())) {
                    holder.cancelButton.setVisibility(View.VISIBLE);
                    holder.cancelButton.setOnClickListener(v -> {
                        cancelAppointment(appointment, holder.itemView.getContext());
                    });
                } else {
                    holder.cancelButton.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredAppointments.size();
    }

    private void updateAppointmentStatus(String appointmentId, String newStatus) {
        // Implement the logic to update the appointment status
        Log.d(TAG, "Updating appointment status: " + appointmentId + " to " + newStatus);
    }

    private void cancelAppointment(Appointment appointment, Context context) {
        // Implement the logic to cancel the appointment
        Log.d(TAG, "Cancelling appointment: " + appointment.getId());
        Toast.makeText(context, "Appointment cancellation logic not implemented", Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTypeText;
        TextView dateTimeText;
        TextView statusText;
        View buttonContainer;
        MaterialButton approveButton;
        MaterialButton rejectButton;
        MaterialButton cancelButton;

        ViewHolder(View view) {
            super(view);
            serviceTypeText = view.findViewById(R.id.serviceTypeText);
            dateTimeText = view.findViewById(R.id.dateTimeText);
            statusText = view.findViewById(R.id.statusText);
            buttonContainer = view.findViewById(R.id.buttonContainer);
            approveButton = view.findViewById(R.id.approveButton);
            rejectButton = view.findViewById(R.id.rejectButton);
            cancelButton = view.findViewById(R.id.cancelButton);
        }
    }
} 