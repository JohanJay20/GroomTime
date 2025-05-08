package com.example.groomtime;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AppointmentFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_form);

        // Initialize views and set up click listeners
        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            // TODO: Implement appointment submission logic
            Toast.makeText(this, "Appointment submitted!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
} 