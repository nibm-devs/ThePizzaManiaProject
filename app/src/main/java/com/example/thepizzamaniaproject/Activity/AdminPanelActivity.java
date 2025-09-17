package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.thepizzamaniaproject.Helper.SessionManager;
import com.example.thepizzamaniaproject.R;

public class AdminPanelActivity extends AppCompatActivity {

    private CardView cvMenu, cvBranches, cvOrders, cvUsers, cvRiders, cvReports;
    private ImageButton btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Check if user is actually an admin (security measure)
        if (!sessionManager.isLoggedIn() || !sessionManager.isAdmin()) {
            // Redirect to login if not authenticated as admin
            goToLoginActivity();
            return;
        }

        // Initialize views
        cvMenu = findViewById(R.id.cvMenu);
        cvBranches = findViewById(R.id.cvBranches);
        cvOrders = findViewById(R.id.cvOrders);
        cvUsers = findViewById(R.id.cvUsers);
        cvRiders = findViewById(R.id.cvRiders);
        cvReports = findViewById(R.id.cvReports);
        btnLogout = findViewById(R.id.btnLogout);

        // Set listeners for each card
        cvMenu.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, AddEditMenuItemActivity.class));
        });

        cvBranches.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, BranchesActivity.class));
        });

        cvOrders.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, DetailsActivity.class));
        });

        cvUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, AdminRegisterActivity.class));
        });

        cvRiders.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, RiderProfileActivity.class));
        });

        cvReports.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, RiderSecurityActivity.class));
        });

        // Set logout button listener
        btnLogout.setOnClickListener(v -> logout());
    }

    // Updated logout method
    private void logout() {
        sessionManager.logoutUser();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(AdminPanelActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}