package com.example.groomtime.services;

import android.util.Log;
import com.example.groomtime.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserService {
    private static final String TAG = "UserService";
    private final DatabaseReference usersRef;

    public UserService() {
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void createUser(FirebaseUser firebaseUser, String role, OnUserCallback callback) {
        try {
            if (firebaseUser == null) {
                callback.onError("Firebase user is null");
                return;
            }

            String uid = firebaseUser.getUid();
            String email = firebaseUser.getEmail();
            String displayName = firebaseUser.getDisplayName();
            String photoUrl = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;

            // Validate required fields
            if (uid == null || email == null) {
                callback.onError("Required user data is missing");
                return;
            }

            User user = new User(
                uid,
                displayName != null ? displayName : "User",
                email,
                role != null ? role : "user",
                photoUrl
            );

            usersRef.child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User created successfully");
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating user: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in createUser: " + e.getMessage());
            callback.onError("Error creating user: " + e.getMessage());
        }
    }

    public void getUserRole(String uid, OnUserCallback callback) {
        try {
            if (uid == null || uid.isEmpty()) {
                callback.onError("User ID is required");
                return;
            }

            usersRef.child(uid).get()
                .addOnSuccessListener(dataSnapshot -> {
                    try {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                callback.onSuccess(user);
                            } else {
                                callback.onError("User data is null");
                            }
                        } else {
                            callback.onError("User not found");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing user data: " + e.getMessage());
                        callback.onError("Error processing user data: " + e.getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in getUserRole: " + e.getMessage());
            callback.onError("Error getting user role: " + e.getMessage());
        }
    }

    public interface OnUserCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }
} 