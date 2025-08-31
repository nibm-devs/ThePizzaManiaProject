package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.thepizzamania.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView cvMenuManagement, cvBranchManagement, cvOrderManagement;
    private CardView cvUserManagement, cvRiderManagement, cvReports;
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
        cvMenuManagement = findViewById(R.id.cvMenuManagement);
        cvBranchManagement = findViewById(R.id.cvBranchManagement);
        cvOrderManagement = findViewById(R.id.cvOrderManagement);
        cvUserManagement = findViewById(R.id.cvUserManagement);
        cvRiderManagement = findViewById(R.id.cvRiderManagement);
        cvReports = findViewById(R.id.cvReports);
    }

    private void setupClickListeners() {
        cvMenuManagement.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, MenuManagementActivity.class));
        });

        cvBranchManagement.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, BranchManagementActivity.class));
        });

        cvOrderManagement.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, OrderManagementActivity.class));
        });

        cvUserManagement.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, UserManagementActivity.class));
        });

        cvRiderManagement.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, RiderManagementActivity.class));
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