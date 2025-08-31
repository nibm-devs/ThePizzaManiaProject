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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.thepizzamaniaproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AdminLoginActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername, editTextPassword;
    private TextInputLayout usernameLayout, passwordLayout;
    private MaterialButton buttonLogin;
    private CardView cardView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Set status bar transparent
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

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
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        usernameLayout = findViewById(R.id.usernameLayout);
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
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameLayout.setError(null);
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
    }

    private boolean validateInputs() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty()) {
            usernameLayout.setError("Admin ID is required");
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
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);

            if (username.equals("admin") && password.equals("admin123")) {
                loginSuccess();
            } else {
                loginFailed();
            }
        }, 2000);
    }

    private void loginSuccess() {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AdminLoginActivity.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void loginFailed() {
        Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();

        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        cardView.startAnimation(shake);
    }
}