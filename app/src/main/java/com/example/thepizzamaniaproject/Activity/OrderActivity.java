package com.example.thepizzamaniaproject.Activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.thepizzamaniaproject.Domain.Order;
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.Helper.CartManager;
import com.example.thepizzamaniaproject.Helper.LocationHelper;
import com.example.thepizzamaniaproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final int MAP_SELECTION_REQUEST = 1002;

    private LocationHelper locationHelper;
    private TextView tvSelectedLocation;
    private EditText editTextAddress;
    private double selectedLatitude;
    private double selectedLongitude;

    private double itemsTotalValue, taxValue, deliveryValue, totalValue;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setupBottomNavigation();

        // Initialize views
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        editTextAddress = findViewById(R.id.editTextAddress);

        // Initialize location helper
        locationHelper = new LocationHelper(this);

        // Set up location buttons
        findViewById(R.id.btnCurrentLocation).setOnClickListener(v -> getCurrentLocation());
        findViewById(R.id.btnSelectOnMap).setOnClickListener(v -> selectOnMap());

        // Set up place order button
        findViewById(R.id.btn_checkout).setOnClickListener(v -> placeOrder());


        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");


        Intent intent = getIntent();
        if (intent != null) {
            itemsTotalValue = intent.getDoubleExtra("itemsTotal", 0);
            taxValue = intent.getDoubleExtra("tax", 0);
            deliveryValue = intent.getDoubleExtra("delivery", 0);
            totalValue = intent.getDoubleExtra("total", 0);

            // Update UI with cart data
            TextView itemsTotalTextView = findViewById(R.id.itemsTotalTextView);
            TextView taxTextView = findViewById(R.id.taxTextView);
            TextView deliveryTextView = findViewById(R.id.deliveryTextView);
            TextView totalTextView = findViewById(R.id.totalTextView);

            itemsTotalTextView.setText("Rs." + String.format("%.2f", itemsTotalValue));
            taxTextView.setText("Rs." + String.format("%.2f", taxValue));
            deliveryTextView.setText("Rs." + String.format("%.2f", deliveryValue));
            totalTextView.setText("Rs." + String.format("%.2f", totalValue));
        }

    }

    private void setupBottomNavigation() {
        findViewById(R.id.homeBtn).setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.profileBtn).setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, ProfileActivity.class));
        });

        findViewById(R.id.cartBtn).setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, CartActivity.class));
        });

        findViewById(R.id.branchesBtn).setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, BranchesActivity.class));
        });
    }

    private void getCurrentLocation() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                selectedLatitude = location.getLatitude();
                selectedLongitude = location.getLongitude();

                // Reverse geocode to get address
                Geocoder geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            selectedLatitude, selectedLongitude, 1);

                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String fullAddress = address.getAddressLine(0);
                        tvSelectedLocation.setText(fullAddress);
                        editTextAddress.setText(fullAddress);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    tvSelectedLocation.setText("Lat: " + selectedLatitude + ", Lng: " + selectedLongitude);
                }
            }

            @Override
            public void onLocationError(String error) {
                Toast.makeText(OrderActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectOnMap() {
        // Implement map selection activity
        Intent intent = new Intent(this, MapSelectionActivity.class);
        startActivityForResult(intent, MAP_SELECTION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_SELECTION_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedLatitude = data.getDoubleExtra("latitude", 0);
            selectedLongitude = data.getDoubleExtra("longitude", 0);
            String address = data.getStringExtra("address");

            tvSelectedLocation.setText(address);
            editTextAddress.setText(address);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void placeOrder() {
        // Get the address from editTextAddress
        String deliveryAddress = editTextAddress.getText().toString().trim();

        if (deliveryAddress.isEmpty()) {
            Toast.makeText(this, "Please enter a delivery address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save order with location data
        saveOrderToDatabase(deliveryAddress, selectedLatitude, selectedLongitude);
    }

    private void saveOrderToDatabase(String address, double lat, double lng) {
        // Get customer details from form
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPhone = findViewById(R.id.editTextPhone);
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);

        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        // Get selected gender
        String gender = "";
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId == R.id.radioMale) {
            gender = "Male";
        } else if (selectedId == R.id.radioFemale) {
            gender = "Female";
        }

        // Validate all fields
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert cart items to map for Firebase
        Map<String, PizzaDomain> cartItemsMap = new HashMap<>();
        List<PizzaDomain> cartItems = CartManager.getInstance().getCartItems();

        for (int i = 0; i < cartItems.size(); i++) {
            cartItemsMap.put("item" + i, cartItems.get(i));
        }

        // Create order object
        Order order = new Order(name, email, phone, gender, address, lat, lng,
                itemsTotalValue, taxValue, deliveryValue, totalValue, cartItemsMap);

        // Generate a unique key for the order
        String orderId = databaseReference.push().getKey();
        order.setOrderId(orderId);

        // Save order to Firebase
        databaseReference.child(orderId).setValue(order.toMap())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(OrderActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

                    // Clear cart after successful order
                    CartManager.getInstance().clearCart();

                    // Navigate to order success activity
                    Intent successIntent = new Intent(OrderActivity.this, OrderSuccessActivity.class);
                    successIntent.putExtra("ORDER_ID", orderId);
                    startActivity(successIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OrderActivity.this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
