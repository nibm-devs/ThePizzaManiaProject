package com.example.thepizzamaniaproject.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.thepizzamaniaproject.Domain.MenuItem;
import com.example.thepizzamaniaproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddEditMenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etDescription, etPrice;
    private Spinner spinnerCategory;
    private Button btnSave, btnSelectImage;
    private ImageView ivPreview;

    private Uri imageUri;
    private MenuItem menuItem;
    private boolean isEditMode = false;

    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_menu_item);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("menu_images");

        // Initialize views
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivPreview = findViewById(R.id.ivPreview);

        // Setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Setup category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Check if we're editing an existing item
        if (getIntent().hasExtra("menuItem")) {
            isEditMode = true;
            menuItem = getIntent().getParcelableExtra("menuItem");
            populateFields();
        } else {
            menuItem = new MenuItem();
        }

        btnSelectImage.setOnClickListener(v -> openImageChooser());
        btnSave.setOnClickListener(v -> saveMenuItem());
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

            // Select the appropriate category in spinner
            ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
            int position = adapter.getPosition(menuItem.getCategory());
            if (position >= 0) {
                spinnerCategory.setSelection(position);
            }
        }
    }

    private void saveMenuItem() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
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
            // Keep existing image if editing and no new image selected
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
            // Update existing item
            db.collection("menuItems").document(menuItem.getId())
                    .set(menuItem)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddEditMenuItemActivity.this, "Menu item updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddEditMenuItemActivity.this, "Error updating item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Create new item
            db.collection("menuItems")
                    .add(menuItem)
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddEditMenuItemActivity.this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddEditMenuItemActivity.this, "Error adding item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}