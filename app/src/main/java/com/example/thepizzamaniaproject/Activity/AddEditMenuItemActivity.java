package com.example.thepizzamaniaproject.Activity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddEditMenuItemActivity extends AppCompatActivity {

    private EditText etPizzaId, etName, etDescription, etPrice, etPictureUrl;
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
        autoCompleteCategory = findViewById(R.id.autoComplete);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
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
            loadExistingData();
        }
    }

    private void loadExistingData() {
        // TODO: If you pass existing pizza data via Intent, populate here
        etPizzaId.setText(existingPizzaId);
        etPizzaId.setEnabled(false); // Don't allow editing ID in edit mode
    }

    private void saveMenuItem() {
        String pizzaId = etPizzaId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String pictureUrl = etPictureUrl.getText().toString().trim();
        String category = autoCompleteCategory.getText().toString().trim();

        if (pizzaId.isEmpty() || name.isEmpty() || description.isEmpty() ||
                priceStr.isEmpty() || pictureUrl.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Build object
        HashMap<String, Object> pizza = new HashMap<>();
        pizza.put("title", name);
        pizza.put("description", description);
        pizza.put("price", price);
        pizza.put("category", category);
        pizza.put("picture", pictureUrl);
        pizza.put("star", 0);
        pizza.put("time", 0);

        // Save to DB
        dbRef.child(pizzaId).setValue(pizza).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, isEditMode ? "Pizza updated!" : "Pizza added!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
