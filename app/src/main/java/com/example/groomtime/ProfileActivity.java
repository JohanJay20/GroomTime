package com.example.groomtime;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private ShapeableImageView profileImage;
    private TextView nameText;
    private TextView emailText;
    private TextView phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profileImage = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);

        // Load user data
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Set name
            String displayName = user.getDisplayName();
            nameText.setText(displayName != null ? displayName : "No name available");

            // Set email
            String email = user.getEmail();
            emailText.setText(email != null ? email : "No email available");

            // Set phone
            String phone = user.getPhoneNumber();
            phoneText.setText(phone != null ? phone : "No phone available");

            // Load profile image
            if (user.getPhotoUrl() != null) {
                Picasso.get()
                    .load(user.getPhotoUrl())
                    .into(profileImage);
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
} 