package com.example.thepizzamaniaproject.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thepizzamaniaproject.R;
import com.google.android.material.textfield.TextInputEditText;

public class RiderRegistrationActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, phoneEditText,
            addressEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_registration);

        // Initialize input fields
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        // Initialize buttons
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        // Register button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RiderRegistrationActivity.this,
                            "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(RiderRegistrationActivity.this,
                        "Registration successful!", Toast.LENGTH_SHORT).show();

                // TODO: Add your database/Firebase registration logic here
            }
        });

        // Login redirect click
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Redirect to login activity
                Toast.makeText(RiderRegistrationActivity.this,
                        "Redirect to login page", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
