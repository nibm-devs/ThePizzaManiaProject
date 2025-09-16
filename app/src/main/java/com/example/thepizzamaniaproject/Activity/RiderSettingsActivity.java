package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thepizzamaniaproject.R;

public class RiderSettingsActivity extends AppCompatActivity {

    LinearLayout rowChangeProfile, rowChangePassword;
    ImageView btnBack; // back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_settings); // your XML file

        rowChangeProfile = findViewById(R.id.rowChangeProfile);
        rowChangePassword = findViewById(R.id.rowChangePassword);
        btnBack = findViewById(R.id.btnBack);

        // Back button → go to previous page
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // closes this activity and returns to the previous one
            }
        });

        // Navigate to Profile page
        rowChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiderSettingsActivity.this, RiderProfileActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to Change Password page
        rowChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiderSettingsActivity.this, RiderSecurityActivity.class);
                startActivity(intent);
            }
        });
    }
}
