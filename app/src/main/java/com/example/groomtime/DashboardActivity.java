package com.example.groomtime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    private TextView welcomeText;
    private MaterialButton bookAppointmentButton;
    private MaterialButton viewAppointmentsButton;
    private MaterialButton signOutButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
        viewAppointmentsButton = findViewById(R.id.viewAppointmentsButton);
        signOutButton = findViewById(R.id.signOutButton);

        // Set welcome message
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            welcomeText.setText("Welcome, " + (displayName != null ? displayName : "User"));
        }

        // Set up click listeners
        bookAppointmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, BookAppointmentActivity.class);
            startActivity(intent);
        });

        viewAppointmentsButton.setOnClickListener(v -> {
            // TODO: Implement view appointments functionality
            // Intent intent = new Intent(DashboardActivity.this, ViewAppointmentsActivity.class);
            // startActivity(intent);
        });

        signOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not signed in, go to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
