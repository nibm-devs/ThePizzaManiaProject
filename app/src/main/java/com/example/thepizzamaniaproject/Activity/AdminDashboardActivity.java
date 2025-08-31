package com.example.thepizzamaniaproject.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.thepizzamaniaproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView cvMenu, cvBranches, cvOrders, cvUsers, cvRiders, cvReports;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        cvMenu = findViewById(R.id.cvMenu);
        cvBranches = findViewById(R.id.cvBranches);
        cvOrders = findViewById(R.id.cvOrders);
        cvUsers = findViewById(R.id.cvUsers);
        cvRiders = findViewById(R.id.cvRiders);
        cvReports = findViewById(R.id.cvReports);
    }

    private void setupClickListeners() {
        cvMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, MenuManagementActivity.class));
        });

        cvBranches.setOnClickListener(v -> {
            startActivity(new Intent(this, BranchManagementActivity.class));
        });

        cvOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderManagementActivity.class));
        });

        cvUsers.setOnClickListener(v -> {
            startActivity(new Intent(this, UserManagementActivity.class));
        });

        cvRiders.setOnClickListener(v -> {
            startActivity(new Intent(this, RiderManagementActivity.class));
        });

        cvReports.setOnClickListener(v -> {
            Toast.makeText(this, "Reports feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }
}