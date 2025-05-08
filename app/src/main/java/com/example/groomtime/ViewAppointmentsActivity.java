package com.example.groomtime;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.groomtime.models.Appointment;
import com.example.groomtime.services.AppointmentService;
import java.util.ArrayList;
import java.util.List;
import com.example.groomtime.adapters.AppointmentAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;

public class ViewAppointmentsActivity extends AppCompatActivity {
    private static final String TAG = "ViewAppointmentsActivity";
    private TabLayout tabLayout;
    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private FirebaseUser currentUser;
    private List<Appointment> allAppointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Appointments");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);

        // Initialize RecyclerView
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(new ArrayList<>(), false);
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Initialize Firebase Auth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to view appointments", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Approved"));
        tabLayout.addTab(tabLayout.newTab().setText("Rejected"));

        // Add tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = null;
                switch (tab.getPosition()) {
                    case 0: // All
                        status = null;
                        break;
                    case 1: // Pending
                        status = "pending";
                        break;
                    case 2: // Approved
                        status = "approved";
                        break;
                    case 3: // Rejected
                        status = "rejected";
                        break;
                }
                filterAppointments(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Load appointments
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            if (currentUser == null || currentUser.getUid() == null) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d(TAG, "Loading appointments for user: " + currentUser.getUid());
            
            // Direct Firebase query
            DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
            Query query = appointmentsRef.orderByChild("userId").equalTo(currentUser.getUid());
            
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    allAppointments.clear();
                    Log.d(TAG, "Firebase data received. Children count: " + snapshot.getChildrenCount());
                    
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            Appointment appointment = dataSnapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                appointment.setId(dataSnapshot.getKey());
                                Log.d(TAG, "Appointment found: " + appointment.getServiceType() + 
                                    " Status: " + appointment.getStatus());
                                allAppointments.add(appointment);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing appointment: " + e.getMessage());
                        }
                    }
                    
                    runOnUiThread(() -> {
                        if (allAppointments.isEmpty()) {
                            Toast.makeText(ViewAppointmentsActivity.this,
                                "No appointments found",
                                Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Loaded " + allAppointments.size() + " appointments");
                            // Apply current filter
                            int selectedTab = tabLayout.getSelectedTabPosition();
                            String status = null;
                            switch (selectedTab) {
                                case 0: status = null; break;
                                case 1: status = "pending"; break;
                                case 2: status = "approved"; break;
                                case 3: status = "rejected"; break;
                            }
                            filterAppointments(status);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database error: " + error.getMessage());
                    Toast.makeText(ViewAppointmentsActivity.this,
                        "Error loading appointments: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in loadAppointments: " + e.getMessage());
            Toast.makeText(this, "Error loading appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void filterAppointments(String status) {
        Log.d(TAG, "Filtering appointments by status: " + status);
        List<Appointment> filteredAppointments = new ArrayList<>();
        
        if (status == null) {
            // Show all appointments
            filteredAppointments.addAll(allAppointments);
        } else {
            // Filter by status
            for (Appointment appointment : allAppointments) {
                if (appointment.getStatus() != null && 
                    appointment.getStatus().equalsIgnoreCase(status)) {
                    filteredAppointments.add(appointment);
                }
            }
        }
        
        Log.d(TAG, "Filtered appointments count: " + filteredAppointments.size());
        appointmentAdapter.updateAppointments(filteredAppointments);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up if needed
    }
} 