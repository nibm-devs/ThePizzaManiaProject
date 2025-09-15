package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import com.example.thepizzamaniaproject.R;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.example.thepizzamaniaproject.Activity.LoginActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import de.hdodenhof.circleimageview.CircleImageView;

public class RiderHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ShapeableImageView profilePic;
    private TextView riderNameTextView;
    private Switch toggleStatus;
    private LinearLayout jobListingLayout;
    private FirebaseUser currentUser;
    private DatabaseReference riderRef, ordersRef;
    private ValueEventListener ordersListener;

    // Navigation header views
    private CircleImageView navHeaderImage;
    private TextView navHeaderName, navHeaderEmail;
    private TextView btnHistory, btnSettings;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);

        // Firebase references
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        String uid = currentUser.getUid();
        riderRef = FirebaseDatabase.getInstance().getReference("riders").child(uid);
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");


        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.nav_view);
        profilePic = findViewById(R.id.profilePic);
        riderNameTextView = findViewById(R.id.riderName);
        toggleStatus = findViewById(R.id.toggleStatus);
        jobListingLayout = findViewById(R.id.jobListingLayout);

        ImageView menuIcon = findViewById(R.id.menuIcon);

        // Navigation header
        View headerView = navView.getHeaderView(0);
        navHeaderImage = headerView.findViewById(R.id.header_image);
        navHeaderName = headerView.findViewById(R.id.header_name);
        navHeaderEmail = headerView.findViewById(R.id.header_email);
        btnHistory = headerView.findViewById(R.id.btn_delivery_history);
        btnSettings = headerView.findViewById(R.id.btn_delivery_settings);
        btnLogout = headerView.findViewById(R.id.btn_logout);

        // Show rider info
        displayRiderInfo(uid);

        // Show navigation header info
        displayNavHeaderInfo();

        // Menu icon click opens drawer
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation drawer actions
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, RiderDeliveryHistoryActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, RiderSettingsActivity.class)));
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Switch: online/offline status
        toggleStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "online" : "offline";
            riderRef.child("status").setValue(status);
        });

        // Set switch status from DB
        riderRef.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                toggleStatus.setChecked("online".equals(status));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // Orders: only show if at least one order exists
        setupOrderListener();

        // (Optional) Drawer menu item clicks (if you have more items)
        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                btnLogout.performClick();
                return true;
            }
            // Add more menu item actions as needed
            return false;
        });
    }

    private void displayRiderInfo(String uid) {
        // Get profile name and image from DB, update views
        riderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String profileUrl = snapshot.child("profileImageUrl").getValue(String.class);

                riderNameTextView.setText(name != null ? name : "Rider");
                // If image URL is set, load it; else set blank/placeholder
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(RiderHomeActivity.this)
                            .load(profileUrl)
                            .placeholder(R.drawable.rider_profile)
                            .into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.rider_profile); // default/empty image
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void displayNavHeaderInfo() {
        // Set name, email and profile image in nav header
        String email = currentUser.getEmail();
        navHeaderEmail.setText(email != null ? email : "rider@example.com");
        riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                navHeaderName.setText(name != null ? name : "Rider");
                String profileUrl = snapshot.child("profileImageUrl").getValue(String.class);
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(RiderHomeActivity.this)
                            .load(profileUrl)
                            .placeholder(R.drawable.rider_profile)
                            .into(navHeaderImage);
                } else {
                    navHeaderImage.setImageResource(R.drawable.rider_profile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void setupOrderListener() {
        // Show orders section only if there are pending requests for this rider
        ordersListener = ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobListingLayout.setVisibility(View.GONE);
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    String riderId = orderSnap.child("riderId").getValue(String.class);
                    String status = orderSnap.child("status").getValue(String.class);
                    // Show if the order is assigned to this rider, and is not completed/cancelled
                    if (currentUser.getUid().equals(riderId)
                            && status != null && status.equals("requested")) {
                        jobListingLayout.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ordersRef != null && ordersListener != null)
            ordersRef.removeEventListener(ordersListener);
    }
}