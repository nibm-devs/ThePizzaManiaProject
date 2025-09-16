package com.example.thepizzamaniaproject.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.example.thepizzamaniaproject.R;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import de.hdodenhof.circleimageview.CircleImageView;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;

public class RiderHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ShapeableImageView profilePic;
    private TextView riderNameTextView;
    private TextView greetingTextView;
    private LinearLayout jobListingLayout;
    private FirebaseUser currentUser;
    private DatabaseReference riderRef, ordersRef;
    private ValueEventListener ordersListener;
    private ValueEventListener riderInfoListener;
    private BroadcastReceiver profileImageReceiver;

    // Navigation header views
    private CircleImageView navHeaderImage;
    private TextView navHeaderName, navHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);

        // Firebase references
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, RiderRegistrationActivity.class));
            finish();
            return;
        }
        String uid = currentUser.getUid();
        riderRef = FirebaseDatabase.getInstance().getReference("riders").child(uid);
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");


        drawerLayout = findViewById(R.id.drawerLayout);
        profilePic = findViewById(R.id.profilePic);
        riderNameTextView = findViewById(R.id.riderName);
        greetingTextView = findViewById(R.id.greetingTextView);
        SwitchCompat toggleStatus = findViewById(R.id.toggleStatus);
        jobListingLayout = findViewById(R.id.jobListingLayout);

        ImageView menuIcon = findViewById(R.id.menuIcon);

        // Custom Drawer Views
        View navDrawerView = findViewById(R.id.nav_view_container);
        navHeaderImage = navDrawerView.findViewById(R.id.header_image);
        navHeaderName = navDrawerView.findViewById(R.id.header_name);
        navHeaderEmail = navDrawerView.findViewById(R.id.header_email);
        TextView navHistory = navDrawerView.findViewById(R.id.nav_history);
        TextView navSettings = navDrawerView.findViewById(R.id.nav_settings);
        Button logoutButton = navDrawerView.findViewById(R.id.nav_logout_button);

        // Set greeting based on time of day
        setGreeting();

        // Setup Real-time Rider Info Listener
        setupRiderInfoListener();

        // Menu icon click opens drawer
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Custom Drawer Item Clicks
        navHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, RiderDeliveryHistoryActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, RiderSettingsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, AdminLoginActivity.class));
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
        setupProfileImageReceiver();
    }

    private void setupProfileImageReceiver() {
        profileImageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String imageUrl = intent.getStringExtra("profileImageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(RiderHomeActivity.this)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.rider_profile)
                            .error(R.drawable.rider_profile)
                            .into(profilePic);
                    Glide.with(RiderHomeActivity.this)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.rider_profile)
                            .error(R.drawable.rider_profile)
                            .into(navHeaderImage);
                }
            }
        };
    }

    private void setGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Good Morning!";
        } else if (hour >= 12 && hour < 18) {
            greeting = "Good Afternoon!";
        } else if (hour >= 18 && hour < 22) {
            greeting = "Good Evening!";
        } else {
            greeting = "Good Night!";
        }
        greetingTextView.setText(greeting);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Add the listener in onStart
        if (riderInfoListener != null && riderRef != null) {
            riderRef.addValueEventListener(riderInfoListener);
        }
        ContextCompat.registerReceiver(this, profileImageReceiver, new IntentFilter("com.example.thepizzamaniaproject.UPDATE_PROFILE_IMAGE"), ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove the listener in onStop
        if (riderInfoListener != null && riderRef != null) {
            riderRef.removeEventListener(riderInfoListener);
        }
        unregisterReceiver(profileImageReceiver);
    }

    private void setupRiderInfoListener() {
        // Set static email
        String email = currentUser.getEmail();
        navHeaderEmail.setText(email != null ? email : "rider@example.com");

        riderInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String profileUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    // Update main content views
                    riderNameTextView.setText(name != null ? name : "Rider");

                    // Update nav drawer header views
                    navHeaderName.setText(name != null ? name : "Rider");

                    // Update both profile images
                    if (profileUrl != null && !profileUrl.isEmpty()) {
                        Glide.with(RiderHomeActivity.this)
                                .load(profileUrl)
                                .placeholder(R.drawable.rider_profile)
                                .error(R.drawable.rider_profile)
                                .into(profilePic);
                        Glide.with(RiderHomeActivity.this)
                                .load(profileUrl)
                                .placeholder(R.drawable.rider_profile)
                                .error(R.drawable.rider_profile)
                                .into(navHeaderImage);
                    } else {
                        profilePic.setImageResource(R.drawable.rider_profile);
                        navHeaderImage.setImageResource(R.drawable.rider_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RiderHomeActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void setupOrderListener() {
        // Show orders section only if there are pending requests for this rider
        Query riderOrdersQuery = ordersRef.orderByChild("riderId").equalTo(currentUser.getUid());
        ordersListener = riderOrdersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobListingLayout.removeAllViews(); // Clear previous views
                boolean hasPendingOrders = false;
                if (snapshot.exists()) {
                    for (DataSnapshot orderSnap : snapshot.getChildren()) {
                        String status = orderSnap.child("status").getValue(String.class);
                        if (status != null && status.equals("requested")) {
                            hasPendingOrders = true;

                            // Inflate the new order card layout
                            View orderCard = getLayoutInflater().inflate(R.layout.order_card, jobListingLayout, false);

                            // Get views from the new card
                            TextView orderId = orderCard.findViewById(R.id.orderId);
                            TextView customerName = orderCard.findViewById(R.id.customerName);
                            TextView deliveryAddress = orderCard.findViewById(R.id.deliveryAddress);
                            ImageButton callButton = orderCard.findViewById(R.id.phone);
                            TextView deliveryTime = orderCard.findViewById(R.id.deliveryTime);

                            // Get data from snapshot
                            String orderIdStr = orderSnap.getKey();
                            String customerNameStr = orderSnap.child("customerName").getValue(String.class);
                            String addressStr = orderSnap.child("address").getValue(String.class);
                            String mobileStr = orderSnap.child("mobile").getValue(String.class);
                            String timeStr = orderSnap.child("time").getValue(String.class); // Assuming a 'time' field exists

                            // Populate the views with order data
                            orderId.setText("#" + orderIdStr);
                            customerName.setText(customerNameStr);
                            deliveryAddress.setText(addressStr);

                            if (timeStr != null) {
                                deliveryTime.setText(timeStr);
                            } else {
                                deliveryTime.setVisibility(View.GONE); // Hide if no time data
                            }

                            // Set up the call button
                            callButton.setOnClickListener(v -> {
                                if (mobileStr != null && !mobileStr.isEmpty()) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mobileStr, null));
                                    startActivity(intent);
                                }
                            });

                            // Add the card to the layout
                            jobListingLayout.addView(orderCard);
                        }
                    }
                }
                jobListingLayout.setVisibility(hasPendingOrders ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ordersRef != null && ordersListener != null) {
            ordersRef.removeEventListener(ordersListener);
        }
        // No need to remove riderInfoListener here as it's handled in onStop
        if (profileImageReceiver != null) {
            unregisterReceiver(profileImageReceiver);
        }
    }
}