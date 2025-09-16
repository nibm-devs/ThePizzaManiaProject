package com.example.thepizzamaniaproject.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thepizzamaniaproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RiderSecurityActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText currentPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private Button changePasswordButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_security);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Views
        btnBack = findViewById(R.id.btnBack);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Back Button Click
        btnBack.setOnClickListener(v -> finish());

        // Change Password Click
        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmNewPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordEditText.setError("Enter current password");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError("Enter new password");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmNewPasswordEditText.setError("Confirm your new password");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmNewPasswordEditText.setError("Passwords do not match");
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate user with current password
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update password
                currentUser.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(RiderSecurityActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish(); // go back
                    } else {
                        Toast.makeText(RiderSecurityActivity.this, "Failed to change password: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(RiderSecurityActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
