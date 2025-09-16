package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.thepizzamaniaproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private MaterialButton buttonLogin;
    private CardView cardView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Set status bar transparent
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupAnimations();
        setupTextWatchers();
        setupButtonClick();

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
    }

    private void initializeViews() {
        editTextEmail = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        emailLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        buttonLogin = findViewById(R.id.buttonLogin);
        cardView = findViewById(R.id.cardView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupAnimations() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        cardView.startAnimation(slideUp);
    }

    private void setupTextWatchers() {
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupButtonClick() {
        buttonLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                attemptLogin();
            }
        });

        findViewById(R.id.textViewForgotPassword).setOnClickListener(v -> {
            Toast.makeText(this, "Password reset feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.DeliveryRegister).setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, RiderRegistrationActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            return false;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        buttonLogin.setEnabled(true);
                        String errorMessage = "Authentication failed.";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(AdminLoginActivity.this, "Error: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                        loginFailed();
                    }
                });
    }

    private void checkUserRole(String uid) {
        databaseReference.child("admins").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User is an admin
                    loginSuccess(AdminPanelActivity.class);
                } else {
                    // Check if user is a rider
                    checkIfRider(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);
                Toast.makeText(AdminLoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfRider(String uid) {
        databaseReference.child("riders").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User is a rider
                    loginSuccess(RiderHomeActivity.class);
                } else {
                    // User role not found
                    progressBar.setVisibility(View.GONE);
                    buttonLogin.setEnabled(true);
                    Toast.makeText(AdminLoginActivity.this, "User role not defined.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut(); // Sign out user as they have no role
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);
                Toast.makeText(AdminLoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginSuccess(Class<?> destinationActivity) {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, destinationActivity);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void loginFailed() {
        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();

        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        cardView.startAnimation(shake);
    }
}