package com.example.groomtime;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.groomtime.models.Appointment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {
    private TextView totalAppointmentsText;
    private TextView completedAppointmentsText;
    private TextView pendingAppointmentsText;
    private TextView cancelledAppointmentsText;
    private TextView monthlyRevenueText;
    private DatabaseReference appointmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Reports");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Initialize views
        totalAppointmentsText = findViewById(R.id.totalAppointmentsText);
        completedAppointmentsText = findViewById(R.id.completedAppointmentsText);
        pendingAppointmentsText = findViewById(R.id.pendingAppointmentsText);
        cancelledAppointmentsText = findViewById(R.id.cancelledAppointmentsText);
        monthlyRevenueText = findViewById(R.id.monthlyRevenueText);

        // Load reports
        loadReports();
    }

    private void loadReports() {
        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                int completed = 0;
                int pending = 0;
                int cancelled = 0;
                double monthlyRevenue = 0.0;

                // Get current month's start and end dates
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date monthStart = calendar.getTime();

                calendar.add(Calendar.MONTH, 1);
                Date monthEnd = calendar.getTime();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        total++;
                        
                        // Count by status
                        switch (appointment.getStatus()) {
                            case "completed":
                                completed++;
                                // Add to monthly revenue if within current month
                                if (appointment.getDateTime() != null && 
                                    appointment.getDateTime().after(monthStart) && 
                                    appointment.getDateTime().before(monthEnd)) {
                                    // Assuming each service costs 500 pesos
                                    monthlyRevenue += 500.0;
                                }
                                break;
                            case "pending":
                                pending++;
                                break;
                            case "cancelled":
                                cancelled++;
                                break;
                        }
                    }
                }

                // Update UI
                totalAppointmentsText.setText(String.format(Locale.getDefault(), "Total: %d", total));
                completedAppointmentsText.setText(String.format(Locale.getDefault(), "Completed: %d", completed));
                pendingAppointmentsText.setText(String.format(Locale.getDefault(), "Pending: %d", pending));
                cancelledAppointmentsText.setText(String.format(Locale.getDefault(), "Cancelled: %d", cancelled));
                monthlyRevenueText.setText(String.format(Locale.getDefault(), "Monthly Revenue: ₱%.2f", monthlyRevenue));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReportsActivity.this,
                    "Error loading reports: " + databaseError.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 