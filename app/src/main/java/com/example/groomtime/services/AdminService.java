package com.example.groomtime.services;

import com.example.groomtime.models.Admin;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminService {
    private static final String TAG = "AdminService";
    private final DatabaseReference adminRef;

    public AdminService() {
        adminRef = FirebaseDatabase.getInstance().getReference("admins");
    }

    public void createAdmin(FirebaseUser user, OnAdminCallback callback) {
        if (user == null) {
            callback.onError("User is null");
            return;
        }

        Admin admin = new Admin(
            user.getUid(),
            user.getEmail(),
            user.getDisplayName()
        );

        adminRef.child(user.getUid())
            .setValue(admin)
            .addOnSuccessListener(aVoid -> callback.onSuccess(admin))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void checkAdminStatus(String uid, OnAdminCheckCallback callback) {
        adminRef.child(uid)
            .get()
            .addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    Admin admin = snapshot.getValue(Admin.class);
                    callback.onAdminCheck(admin != null && admin.isActive());
                } else {
                    callback.onAdminCheck(false);
                }
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface OnAdminCallback {
        void onSuccess(Admin admin);
        void onError(String error);
    }

    public interface OnAdminCheckCallback {
        void onAdminCheck(boolean isAdmin);
        void onError(String error);
    }
} 