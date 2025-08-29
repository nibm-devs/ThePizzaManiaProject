package com.example.thepizzamaniaproject.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thepizzamaniaproject.Domain.UserDomain;
import com.example.thepizzamaniaproject.Helper.DatabaseHelper;
import com.example.thepizzamaniaproject.Helper.SessionManager;
import com.example.thepizzamaniaproject.R;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {


    private TextInputEditText nameEditText, emailEditText, phoneEditText, addressEditText;
    private TextInputEditText passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;
    private RadioGroup radioGroupGender;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });



        // Rotaion Registration in login activity

        ImageView pizzaImage = findViewById(R.id.logoPicture);


        ObjectAnimator slowRotate = ObjectAnimator.ofFloat(pizzaImage, "rotation", 0f, 360f);
        slowRotate.setDuration(60000); // 60 seconds = 1 rotation per minute
        slowRotate.setInterpolator(new LinearInterpolator());
        slowRotate.setRepeatCount(ObjectAnimator.INFINITE); // loop forever
        slowRotate.start();




        // Initialize helpers
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initializeViews();
        setupClickListeners();


    }


    private void initializeViews()
    {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
        radioGroupGender = findViewById(R.id.radioGroupGender);
    }

    private void setupClickListeners()
    {
        registerButton.setOnClickListener(v -> registerUser());
        loginTextView.setOnClickListener(v -> goToLoginActivity());
    }


    private String getSelectedGender() {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();

        if (selectedId == R.id.radioMale) {
            return "Male";
        } else if (selectedId == R.id.radioFemale) {
            return "Female";
        }
        return "Male"; // Default
    }
    private void registerUser()
    {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String gender = getSelectedGender();

        // Validation
        if (name.isEmpty())
        {
            nameEditText.setError("Please enter your name");
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditText.setError("Please enter a valid email");
            return;
        }

        if (phone.isEmpty())
        {
            phoneEditText.setError("Please enter your phone number");
            return;
        }

        if (address.isEmpty())
        {
            addressEditText.setError("Please enter your address");
            return;
        }

        if (password.isEmpty())
        {
            passwordEditText.setError("Please enter a password");
            return;
        }

        if (password.length() < 6)
        {
            passwordEditText.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword))
        {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Check if email already exists
        if (databaseHelper.checkUserExists(email))
        {
            emailEditText.setError("Email already registered");
            return;
        }

        // Create new user (ID will be auto-generated by database)
        UserDomain newUser = new UserDomain(0, name, email, password, phone,
                address, gender, null);


        // Add to database
        long result = databaseHelper.addUser(newUser);

        if (result != -1)
        {
            // Get the created user to get the auto-generated ID
            UserDomain createdUser = databaseHelper.getUserByEmail(email);
            if (createdUser != null)
            {
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
                goToLoginActivity();
            }
        }
        else
        {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}