package com.example.thepizzamaniaproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.thepizzamaniaproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddEditMenuItemActivity extends AppCompatActivity {

    private EditText etPizzaId, etName, etDescription, etPrice, etPictureUrl, etStar, etTime;
    private AutoCompleteTextView autoCompleteCategory;
    private Button btnSave;
    private ImageButton btnBack;

    private DatabaseReference dbRef;
    private ProgressDialog progressDialog;

    private boolean isEditMode = false;
    private String existingPizzaId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_menu_item);

        // Firebase DB reference
        dbRef = FirebaseDatabase.getInstance().getReference("pizzas");

        // Init views
        etPizzaId = findViewById(R.id.etPizzaId);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etPictureUrl = findViewById(R.id.etPictureUrl);
        etStar = findViewById(R.id.etStar);
        etTime = findViewById(R.id.etTime);
        autoCompleteCategory = findViewById(R.id.autoComplete);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Category dropdown setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_dropdown_item_1line);
        autoCompleteCategory.setAdapter(adapter);

        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Save button
        btnSave.setOnClickListener(v -> saveMenuItem());

        // If editing
        if (getIntent().hasExtra("pizzaId")) {
            isEditMode = true;
            existingPizzaId = getIntent().getStringExtra("pizzaId");
            etPizzaId.setText(existingPizzaId);
            etPizzaId.setEnabled(false); // Don't allow editing ID in edit mode
            loadExistingData();
        }
    }

    private void loadExistingData() {
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        dbRef.child(existingPizzaId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    String name = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    double price = snapshot.child("price").getValue(Double.class);
                    String category = snapshot.child("category").getValue(String.class);
                    String pictureUrl = snapshot.child("picture").getValue(String.class);
                    double star = snapshot.child("star").getValue(Double.class);
                    long time = snapshot.child("time").getValue(Long.class);

                    etName.setText(name);
                    etDescription.setText(description);
                    etPrice.setText(String.valueOf(price));
                    autoCompleteCategory.setText(category);
                    etPictureUrl.setText(pictureUrl);
                    etStar.setText(String.valueOf(star));
                    etTime.setText(String.valueOf(time));

                } else {
                    Toast.makeText(AddEditMenuItemActivity.this, "Pizza data not found!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity if data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AddEditMenuItemActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveMenuItem() {
        String pizzaId = etPizzaId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String pictureUrl = etPictureUrl.getText().toString().trim();
        String category = autoCompleteCategory.getText().toString().trim();
        String starStr = etStar.getText().toString().trim();
        String timeStr = etTime.getText().toString().trim();

        // 1. Basic validation
        if (pizzaId.isEmpty()) {
            Toast.makeText(this, "Please enter a Pizza ID.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a Pizza Name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a Description.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (priceStr.isEmpty()) {
            Toast.makeText(this, "Please enter a Price.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pictureUrl.isEmpty()) {
            Toast.makeText(this, "Please enter a Picture URL.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category.isEmpty()) {
            Toast.makeText(this, "Please select a Category.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        double star;
        int time;
        try {
            price = Double.parseDouble(priceStr);
            star = Double.parseDouble(starStr);
            time = Integer.parseInt(timeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numeric values for Price, Star, and Time.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate star and time ranges
        if (star < 0 || star > 5) {
            Toast.makeText(this, "Star rating must be between 0 and 5.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time < 0) {
            Toast.makeText(this, "Time must be a positive number.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Saving...");
        progressDialog.show();

        // Build object
        HashMap<String, Object> pizza = new HashMap<>();
        pizza.put("title", name);
        pizza.put("description", description);
        pizza.put("price", price);
        pizza.put("category", category);
        pizza.put("picture", pictureUrl);
        pizza.put("star", star);
        pizza.put("time", time);

        // Save to DB
        dbRef.child(pizzaId).setValue(pizza).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, isEditMode ? "Pizza updated! ✅" : "Pizza added! ✅", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}