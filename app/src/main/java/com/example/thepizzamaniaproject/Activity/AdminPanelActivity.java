package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.thepizzamaniaproject.R;

public class AdminPanelActivity extends AppCompatActivity {

    CardView cvMenu, cvBranches, cvOrders, cvUsers, cvRiders, cvReports;
    ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // ✅ Initialize views
        cvMenu = findViewById(R.id.cvMenu);
        cvBranches = findViewById(R.id.cvBranches);
        cvOrders = findViewById(R.id.cvOrders);
        cvUsers = findViewById(R.id.cvUsers);
        cvRiders = findViewById(R.id.cvRiders);
        cvReports = findViewById(R.id.cvReports);
        btnLogout = findViewById(R.id.btnLogout);

        // ✅ Set listeners for each card
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

        // ✅ Logout button → back to login screen
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
            startActivity(intent);
            finish();
        });
    }
}
