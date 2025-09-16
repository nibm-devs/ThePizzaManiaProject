package com.example.thepizzamaniaproject.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thepizzamaniaproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RiderDeliveryHistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;
    private DatabaseReference ordersRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rider_delivery_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        historyContainer = findViewById(R.id.historyContainer);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        if (currentUser != null) {
            loadDeliveryHistory();
        } else {
            Toast.makeText(this, "You need to be logged in to see your history.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadDeliveryHistory() {
        String riderId = currentUser.getUid();
        Query historyQuery = ordersRef.orderByChild("riderId").equalTo(riderId);

        historyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyContainer.removeAllViews(); // Clear old views
                boolean historyFound = false;
                if (snapshot.exists()) {
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        String status = orderSnapshot.child("status").getValue(String.class);

                        // Only show orders that are completed
                        if (status != null && status.equals("completed")) {
                            historyFound = true;
                            LayoutInflater inflater = LayoutInflater.from(RiderDeliveryHistoryActivity.this);
                            View orderCard = inflater.inflate(R.layout.history_order_card, historyContainer, false);

                            // Get views from the card
                            TextView orderIdTv = orderCard.findViewById(R.id.orderId);
                            TextView customerNameTv = orderCard.findViewById(R.id.customerName);
                            TextView addressTv = orderCard.findViewById(R.id.deliveryAddress);
                            TextView dateTv = orderCard.findViewById(R.id.deliveryDate);
                            TextView earningTv = orderCard.findViewById(R.id.earningAmount);

                            // Get data from snapshot
                            String orderId = orderSnapshot.getKey();
                            String customerName = orderSnapshot.child("customerName").getValue(String.class);
                            String address = orderSnapshot.child("address").getValue(String.class);
                            String date = orderSnapshot.child("date").getValue(String.class);
                            Double earning = orderSnapshot.child("earning").getValue(Double.class);

                            // Set data to views
                            orderIdTv.setText("#" + orderId);
                            customerNameTv.setText(customerName);
                            addressTv.setText(address);
                            dateTv.setText(date);
                            if (earning != null) {
                                earningTv.setText(String.format("$%.2f", earning));
                            } else {
                                earningTv.setText("$0.00");
                            }

                            historyContainer.addView(orderCard);
                        }
                    }
                }

                if (!historyFound) {
                    Toast.makeText(RiderDeliveryHistoryActivity.this, "No delivery history found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RiderDeliveryHistoryActivity.this, "Failed to load history.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}