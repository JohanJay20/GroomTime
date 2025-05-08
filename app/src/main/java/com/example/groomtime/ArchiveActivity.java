package com.example.groomtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.example.groomtime.models.Appointment;
import com.example.groomtime.adapters.AppointmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {
    private static final String TAG = "ArchiveActivity";
    private RecyclerView archiveRecyclerView;
    private AppointmentAdapter archiveAdapter;
    private List<Appointment> archivedAppointments;
    private FirebaseAuth mAuth;
    private DatabaseReference appointmentsRef;
    private boolean isAdminView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Initialize views
        archiveRecyclerView = findViewById(R.id.archiveRecyclerView);
        archiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data
        archivedAppointments = new ArrayList<>();
        isAdminView = getIntent().getBooleanExtra("isAdminView", false);
        archiveAdapter = new AppointmentAdapter(archivedAppointments, isAdminView);
        archiveRecyclerView.setAdapter(archiveAdapter);

        // Load archived appointments
        loadArchivedAppointments();
    }

    private void loadArchivedAppointments() {
        String userId = mAuth.getCurrentUser().getUid();
        Query query;
        
        if (isAdminView) {
            // Admin view - show all archived appointments
            query = appointmentsRef.orderByChild("status").equalTo("archived");
        } else {
            // User view - show only user's archived appointments
            query = appointmentsRef.orderByChild("userId").equalTo(userId);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                archivedAppointments.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    if (appointment != null && "archived".equalsIgnoreCase(appointment.getStatus())) {
                        archivedAppointments.add(appointment);
                    }
                }
                archiveAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading archived appointments: " + error.getMessage());
                Toast.makeText(ArchiveActivity.this, 
                    "Error loading archived appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 