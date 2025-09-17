package com.example.thepizzamaniaproject.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thepizzamaniaproject.Helper.DatabaseHelper;
import com.example.thepizzamaniaproject.Helper.SessionManager;
import com.example.thepizzamaniaproject.R;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {



    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });



        // Rotaion animation in login activity

        ImageView pizzaImage = findViewById(R.id.logoPicture);


        ObjectAnimator slowRotate = ObjectAnimator.ofFloat(pizzaImage, "rotation", 0f, 360f);
        slowRotate.setDuration(60000); // 60 seconds = 1 rotation per minute
        slowRotate.setInterpolator(new LinearInterpolator());
        slowRotate.setRepeatCount(ObjectAnimator.INFINITE); // loop forever
        slowRotate.start();




        // Initialize helpers
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

//        // Check if user is already logged in
//        if (sessionManager.isLoggedIn())
//        {
//            goToHomeActivity();
//            return;
//        }

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            if (sessionManager.isAdmin()) {
                goToAdminPanelActivity();
            } else {
                goToHomeActivity();
            }
            return;
        }

        initializeViews();
        setupClickListeners();
    }



    private void initializeViews()
    {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
    }

    private void setupClickListeners()
    {
        loginButton.setOnClickListener(v -> loginUser());
        registerTextView.setOnClickListener(v -> goToRegisterActivity());
    }

//    private void loginUser()
//    {
//        String email = emailEditText.getText().toString().trim();
//        String password = passwordEditText.getText().toString().trim();
//
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Check if user exists and password matches
//        if (databaseHelper.checkUser(email, password))
//        {
//            // Get user details
//            com.example.thepizzamaniaproject.Domain.UserDomain user = databaseHelper.getUserByEmail(email);
//            if (user != null)
//            {
//                sessionManager.createLoginSession(email, user.getName(), user.getId());
//                goToHomeActivity();
//            }
//        }
//        else
//        {
//            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void goToMainActivity()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToRegisterActivity()
    {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void goToHomeActivity()
    {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToAdminPanelActivity() {
        Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for admin login first
        if (email.equals("admin1") && password.equals("admin123")) {
            sessionManager.createAdminSession(); // Create a new method for admin sessions
            goToAdminPanelActivity();
            return;
        }

        // Existing customer login check
        if (databaseHelper.checkUser(email, password)) {
            com.example.thepizzamaniaproject.Domain.UserDomain user = databaseHelper.getUserByEmail(email);
            if (user != null) {
                sessionManager.createLoginSession(email, user.getName(), user.getId());
                goToHomeActivity();
            }
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }


}