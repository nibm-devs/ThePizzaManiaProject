package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thepizzamaniaproject.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RiderRegistrationActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, phoneEditText,
            addressEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_registration);

        FirebaseApp.initializeApp(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Riders");


        // Initialize input fields
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        // Initialize buttons
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginTextView = findViewById(R.id.loginTextView);

        // Register button click
        registerButton.setOnClickListener(v -> {

            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Simple validation
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() ||
                    username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RiderRegistrationActivity.this,
                        "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Enter a valid email");
                emailEditText.requestFocus();
                return;
            }

            if (!Patterns.PHONE.matcher(phone).matches()) {
                phoneEditText.setError("Enter a valid phone number");
                phoneEditText.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RiderRegistrationActivity.this,
                        "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Save additional info to Realtime Database
                                String userId = firebaseUser.getUid();
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name", name);
                                userMap.put("email", email);
                                userMap.put("phone", phone);
                                userMap.put("address", address);
                                userMap.put("username", username);

                                databaseReference.child(userId).setValue(userMap)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(RiderRegistrationActivity.this,
                                                        "Registration successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RiderRegistrationActivity.this, AdminLoginActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(RiderRegistrationActivity.this,
                                                        "Database error: " + dbTask.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(RiderRegistrationActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Login redirect click
        loginTextView.setOnClickListener(v ->
                startActivity(new Intent(RiderRegistrationActivity.this, AdminLoginActivity.class))
        );
    }
}
