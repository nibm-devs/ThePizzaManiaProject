package com.example.thepizzamaniaproject.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thepizzamaniaproject.Domain.MenuItem;
import com.example.thepizzamaniaproject.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AddEditMenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private MenuItem menuItem;
    private boolean isEditMode = false;

    // View references
    private EditText etName, etDescription, etPrice;
    private AutoCompleteTextView autoCompleteCategory;
    private Button btnSave;
    private FrameLayout btnSelectImage;
    private ImageButton btnBack;
    private ImageView ivPreview;

    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_menu_item);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("menu_images");

        // Initialize views using findViewById
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        autoCompleteCategory = findViewById(R.id.autoComplete);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnBack = findViewById(R.id.btnBack);
        ivPreview = findViewById(R.id.ivPreview);

        // Setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Setup category AutoCompleteTextView
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_dropdown_item_1line);
        autoCompleteCategory.setAdapter(adapter);

        // Set click listeners
        btnBack.setOnClickListener(v -> onBackPressed());
        btnSelectImage.setOnClickListener(v -> openImageChooser());
        btnSave.setOnClickListener(v -> saveMenuItem());

        // Check if we're editing an existing item
        if (getIntent().hasExtra("menuItem")) {
            isEditMode = true;
            menuItem = getIntent().getParcelableExtra("menuItem");
            populateFields();
        } else {
            menuItem = new MenuItem();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivPreview.setImageURI(imageUri);
            ivPreview.setVisibility(View.VISIBLE);
        }
    }

    private void populateFields() {
        if (menuItem != null) {
            etName.setText(menuItem.getName());
            etDescription.setText(menuItem.getDescription());
            etPrice.setText(String.valueOf(menuItem.getPrice()));

            // Set category in AutoCompleteTextView
            autoCompleteCategory.setText(menuItem.getCategory());

            // Show existing image if available
            if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
                ivPreview.setVisibility(View.VISIBLE);
            }
        }
    }

    private void saveMenuItem() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String category = autoCompleteCategory.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setCategory(category);

        if (imageUri != null) {
            uploadImageAndSaveItem();
        } else if (isEditMode && menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
            saveMenuItemToFirebase(menuItem.getImageUrl());
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageAndSaveItem() {
        progressDialog.show();
        final String imageName = UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageReference.child(imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveMenuItemToFirebase(imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddEditMenuItemActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveMenuItemToFirebase(String imageUrl) {
        menuItem.setImageUrl(imageUrl);

        if (isEditMode) {
            db.collection("menuItems").document(menuItem.getId())
                    .set(menuItem)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Menu item updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Error updating item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            db.collection("menuItems")
                    .add(menuItem)
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Error adding item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}