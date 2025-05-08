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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {
    private TextView welcomeText;
    private MaterialButton bookAppointmentButton;
    private MaterialButton viewAppointmentsButton;
    private FirebaseAuth mAuth;
    private ShapeableImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
        viewAppointmentsButton = findViewById(R.id.viewAppointmentsButton);

        // Set welcome message and load profile image
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
            try {
                Intent intent = new Intent(DashboardActivity.this, ViewAppointmentsActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(DashboardActivity.this, 
                    "Error opening appointments: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
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
            Log.e("DashboardActivity", "Error during logout: " + e.getMessage());
            // Even if there's an error, try to redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
