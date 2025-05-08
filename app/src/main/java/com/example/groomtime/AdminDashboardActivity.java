package com.example.groomtime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.example.groomtime.adapters.AppointmentAdapter;
import com.example.groomtime.models.Appointment;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private static final String TAG = "AdminDashboardActivity";
    private TabLayout tabLayout;
    private RecyclerView appointmentsRecyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ShapeableImageView profileImage;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> allAppointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);

        // Initialize RecyclerView
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(new ArrayList<>(), true); // true for admin view
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

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
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        
        appointmentsRef.addValueEventListener(new ValueEventListener() {
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
                        Toast.makeText(AdminDashboardActivity.this,
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
                Toast.makeText(AdminDashboardActivity.this,
                    "Error loading appointments: " + error.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        
        // Get the profile image view from the menu item
        MenuItem profileItem = menu.findItem(R.id.action_account);
        View actionView = profileItem.getActionView();
        if (actionView != null) {
            profileImage = actionView.findViewById(R.id.profileImage);
            loadProfileImage();
            
            // Set click listener for the profile image
            profileImage.setOnClickListener(v -> showPopupMenu(v));
        }
        
        return true;
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.action_logout) {
                handleLogout();
                return true;
            }
            return false;
        });
        
        popup.show();
    }

    private void loadProfileImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getPhotoUrl() != null) {
            Picasso.get()
                .load(currentUser.getPhotoUrl())
                .into(profileImage);
        }
    }

    private void handleLogout() {
        try {
            // Get Firebase instance
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            
            // Sign out from Firebase
            mAuth.signOut();
            
            // Get Google Sign In client
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build());
            
            // Sign out from Google
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                // Clear any stored user data
                getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
                
                // Navigate back to login screen
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            // Even if there's an error, try to redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in and is admin
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