package com.example.groomtime.services;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.Task;

public class RecaptchaService {
    private static final String TAG = "RecaptchaService";
    private static final String SITE_KEY = "YOUR_SITE_KEY"; // Replace with your reCAPTCHA site key

    public interface RecaptchaCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public void verifyRecaptcha(Context context, RecaptchaCallback callback) {
        SafetyNet.getClient(context).verifyWithRecaptcha(SITE_KEY)
            .addOnSuccessListener(response -> {
                if (response.getTokenResult() != null && !response.getTokenResult().isEmpty()) {
                    // Token received, verification successful
                    Log.d(TAG, "reCAPTCHA verification successful");
                    callback.onSuccess();
                } else {
                    Log.e(TAG, "reCAPTCHA verification failed: Empty token");
                    callback.onError("Verification failed. Please try again.");
                }
            })
            .addOnFailureListener(e -> {
                String errorMessage;
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    errorMessage = "Error: " + CommonStatusCodes.getStatusCodeString(apiException.getStatusCode());
                } else {
                    errorMessage = "Error: " + e.getMessage();
                }
                Log.e(TAG, "reCAPTCHA verification failed: " + errorMessage);
                callback.onError(errorMessage);
            });
    }
} 