package com.example.groomtime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.groomtime.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.example.groomtime.services.UserService;
import com.google.firebase.database.DataSnapshot;
import com.example.groomtime.services.AdminService;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SignInButton btnGoogleSignIn;
    private AdminService adminService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            // Initialize Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            // Initialize Sign In Button
            btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
            if (btnGoogleSignIn == null) {
                Log.e(TAG, "SignInButton not found in layout");
                return;
            }

            btnGoogleSignIn.setSize(SignInButton.SIZE_WIDE);
            btnGoogleSignIn.setColorScheme(SignInButton.COLOR_LIGHT);

            btnGoogleSignIn.setOnClickListener(v -> signIn());

            adminService = new AdminService();

            // Check if user is already signed in
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                checkUserRole(currentUser);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void signIn() {
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            Log.e(TAG, "Error in signIn: " + e.getMessage(), e);
            Toast.makeText(this, "Error starting sign in: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google sign in failed: " + e.getStatusCode(), e);
                Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(),
                    Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error in onActivityResult: " + e.getMessage(), e);
                Toast.makeText(this, "Error processing sign in: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user);
                        } else {
                            Log.e(TAG, "User is null after successful sign in");
                            Toast.makeText(LoginActivity.this, "Error: User is null",
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed: " +
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                            Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in firebaseAuthWithGoogle: " + e.getMessage(), e);
            Toast.makeText(this, "Error authenticating with Firebase: " + e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserRole(FirebaseUser user) {
        try {
            mDatabase.child("users").child(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String role = task.getResult().child("role").getValue(String.class);
                            Log.d(TAG, "User role: " + role);
                            
                            Intent intent;
                            if ("admin".equals(role)) {
                                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            createNewUser(user);
                        }
                    } else {
                        Log.e(TAG, "Error checking user role", task.getException());
                        Toast.makeText(LoginActivity.this, "Error checking user role: " +
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                            Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in checkUserRole: " + e.getMessage(), e);
            Toast.makeText(this, "Error checking user role: " + e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewUser(FirebaseUser firebaseUser) {
        try {
            DatabaseReference userRef = mDatabase.child("users").child(firebaseUser.getUid());
            
            User user = new User(
                firebaseUser.getUid(),
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                "user",
                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null
            );

            userRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating user", e);
                    Toast.makeText(LoginActivity.this, "Error creating user account: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in createNewUser: " + e.getMessage(), e);
            Toast.makeText(this, "Error creating new user: " + e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 2404).show();
            }
            return false;
        }
        return true;
    }

    // Add this method to handle sign out
    public void signOut() {
        // Sign out from Firebase
        mAuth.signOut();
        
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
    }
}
