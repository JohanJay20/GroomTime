package com.example.groomtime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.groomtime.models.Appointment;
import com.example.groomtime.services.AppointmentService;
import java.util.Calendar;
import java.util.Date;

public class BookAppointmentActivity extends AppCompatActivity {
    private AutoCompleteTextView serviceTypeInput;
    private TextInputEditText dateInput;
    private TextInputEditText timeInput;
    private TextInputEditText notesInput;
    private MaterialButton bookButton;
    private AppointmentService appointmentService;
    private Calendar selectedDateTime;

    private final String[] serviceTypes = {
        "Haircut",
        "Hair Coloring",
        "Hair Styling",
        "Facial",
        "Manicure",
        "Pedicure",
        "Massage"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Initialize views
        serviceTypeInput = findViewById(R.id.serviceTypeInput);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        notesInput = findViewById(R.id.notesInput);
        bookButton = findViewById(R.id.bookButton);

        // Initialize services
        appointmentService = new AppointmentService();
        selectedDateTime = Calendar.getInstance();

        // Setup service type dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, serviceTypes);
        serviceTypeInput.setAdapter(adapter);

        // Setup date picker
        dateInput.setOnClickListener(v -> showDatePicker());

        // Setup time picker
        timeInput.setOnClickListener(v -> showTimePicker());

        // Setup book button
        bookButton.setOnClickListener(v -> verifyAndBookAppointment());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateInput.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            },
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                timeInput.setText(String.format("%02d:%02d", hourOfDay, minute));
            },
            selectedDateTime.get(Calendar.HOUR_OF_DAY),
            selectedDateTime.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void verifyAndBookAppointment() {
        // First verify the input
        if (!validateInput()) {
            return;
        }

        // Show loading state
        bookButton.setEnabled(false);
        bookButton.setText("Booking...");

        // Proceed directly with booking
        bookAppointment();
    }

    private boolean validateInput() {
        String serviceType = serviceTypeInput.getText().toString();
        String notes = notesInput.getText().toString();

        if (serviceType.isEmpty()) {
            Toast.makeText(this, "Please select a service type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dateInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (timeInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return false;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to book an appointment", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void bookAppointment() {
        String serviceType = serviceTypeInput.getText().toString();
        String notes = notesInput.getText().toString();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Appointment appointment = new Appointment(
            null, // ID will be generated by Firebase
            currentUser.getUid(),
            currentUser.getDisplayName(),
            serviceType,
            selectedDateTime.getTime(),
            "pending",
            notes
        );

        appointmentService.createAppointment(appointment, new AppointmentService.OnAppointmentCallback() {
            @Override
            public void onSuccess(Appointment appointment) {
                runOnUiThread(() -> {
                    Toast.makeText(BookAppointmentActivity.this, 
                        "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(BookAppointmentActivity.this,
                        "Error booking appointment: " + errorMessage, Toast.LENGTH_LONG).show();
                    bookButton.setEnabled(true);
                    bookButton.setText("Book Appointment");
                });
            }
        });
    }
} 