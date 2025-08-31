package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thepizzamaniaproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminRegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextFullName, editTextEmail, editTextPhone,
            editTextPassword, editTextConfirmPassword, editTextAdminId;
    private TextInputLayout fullNameLayout, emailLayout, phoneLayout,
            passwordLayout, confirmPasswordLayout, adminIdLayout;
    private MaterialButton buttonRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextAdminId = findViewById(R.id.editTextAdminId);

        fullNameLayout = findViewById(R.id.fullNameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        adminIdLayout = findViewById(R.id.adminIdLayout);

        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        buttonRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                registerAdmin();
            }
        });

        findViewById(R.id.textViewLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String adminId = editTextAdminId.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            fullNameLayout.setError("Full name is required");
            isValid = false;
        } else {
            fullNameLayout.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email address");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Phone number is required");
            isValid = false;
        } else if (phone.length() < 10) {
            phoneLayout.setError("Enter a valid phone number");
            isValid = false;
        } else {
            phoneLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Please confirm your password");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        if (TextUtils.isEmpty(adminId)) {
            adminIdLayout.setError("Admin ID is required");
            isValid = false;
        } else {
            adminIdLayout.setError(null);
        }

        return isValid;
    }

    private void registerAdmin() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String adminId = editTextAdminId.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                saveAdminData(user.getUid(), fullName, email, phone, adminId);
                                            }
                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            buttonRegister.setEnabled(true);
                            Toast.makeText(AdminRegisterActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveAdminData(String userId, String fullName, String email, String phone, String adminId) {
        HashMap<String, Object> adminData = new HashMap<>();
        adminData.put("fullName", fullName);
        adminData.put("email", email);
        adminData.put("phone", phone);
        adminData.put("adminId", adminId);
        adminData.put("userType", "admin");
        adminData.put("timestamp", System.currentTimeMillis());

        mDatabase.child("users").child(userId).setValue(adminData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        buttonRegister.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(AdminRegisterActivity.this,
                                    "Registration successful!", Toast.LENGTH_SHORT).show();

                            sendEmailVerification();

                            startActivity(new Intent(AdminRegisterActivity.this, AdminLoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(AdminRegisterActivity.this,
                                    "Failed to save admin data: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AdminRegisterActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}