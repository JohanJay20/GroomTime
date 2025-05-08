package com.example.groomtime;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.groomtime.models.Appointment;
import java.util.ArrayList;
import java.util.List;

public class AdminViewAppointmentsActivity extends AppCompatActivity {
    private RecyclerView appointmentsRecyclerView;
    private DatabaseReference appointmentsRef;
    private List<Appointment> appointmentsList;
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_appointments);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Appointments");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Initialize views
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);

        // Set up RecyclerView
        appointmentsList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentsList, true); // true for admin view
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Load appointments
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appointmentsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentsList.add(appointment);
                    }
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdminViewAppointmentsActivity.this,
                    "Error loading appointments: " + databaseError.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 