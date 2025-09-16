package com.example.thepizzamaniaproject.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.thepizzamaniaproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RiderProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText editTextName, editTextEmail, editTextPhone, editTextAddress;
    private TextView profileName;

    private DatabaseReference riderRef;

    private Uri imageUri;
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private Uri cameraImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rider_profile);

        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        profileImage = findViewById(R.id.profileImage);
        ImageButton editImageButton = findViewById(R.id.editImageButton);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        Button saveButton = findViewById(R.id.saveButton);
        profileName = findViewById(R.id.profileName);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            riderRef = FirebaseDatabase.getInstance().getReference().child("riders").child(currentUserId);
            loadRiderData();
        } else {
            // Handle user not logged in
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Glide.with(RiderProfileActivity.this).load(imageUri).into(profileImage);
                    }
                });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        imageUri = cameraImageUri;
                        Glide.with(RiderProfileActivity.this).load(imageUri).into(profileImage);
                    }
                });

        editImageButton.setOnClickListener(v -> showPhotoChoiceDialog());

        saveButton.setOnClickListener(v -> {
            if (imageUri != null) {
                // If a new image is selected, upload it first.
                uploadToCloudinaryAndSave();
            } else {
                // Otherwise, just save the other information.
                saveRiderInformation(null);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Load rider data every time the activity starts.
        loadRiderData();
    }

    private void loadRiderData() {
        riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String image = snapshot.child("profileImageUrl").getValue(String.class);

                    editTextName.setText(name);
                    profileName.setText(name);
                    editTextEmail.setText(email);
                    editTextPhone.setText(phone);
                    editTextAddress.setText(address);

                    if (image != null && !image.isEmpty()) {
                        Glide.with(RiderProfileActivity.this)
                                .load(image)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RiderProfileActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToCloudinaryAndSave() {
        MediaManager.get().upload(imageUri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Toast.makeText(RiderProfileActivity.this, "Upload starting...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                // Optionally handle progress
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String imageUrl = (String) resultData.get("url");
                saveRiderInformation(imageUrl);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Toast.makeText(RiderProfileActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                // Optionally handle reschedule
            }
        }).dispatch();
    }

    private void saveRiderInformation(String imageUrl) {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> riderMap = new HashMap<>();
        riderMap.put("name", name);
        riderMap.put("email", email);
        riderMap.put("phone", phone);
        riderMap.put("address", address);
        if (imageUrl != null) {
            riderMap.put("profileImageUrl", imageUrl);
        }

        riderRef.updateChildren(riderMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RiderProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                if (imageUrl != null) {
                    // Also update the image in the home activity
                    updateHomePageProfileImage(imageUrl);
                }
                finish(); // Go back to the previous activity
            } else {
                Toast.makeText(RiderProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHomePageProfileImage(String imageUrl) {
        // This is a simplified example. You might need a more robust way to communicate
        // between activities, like using a shared ViewModel, a broadcast, or an event bus.
        Intent intent = new Intent("com.example.thepizzamaniaproject.UPDATE_PROFILE_IMAGE");
        intent.putExtra("profileImageUrl", imageUrl);
        sendBroadcast(intent);
    }

    private void showPhotoChoiceDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.photo_choice_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button cameraButton = dialog.findViewById(R.id.cameraButton);
        Button galleryButton = dialog.findViewById(R.id.galleryButton);

        cameraButton.setOnClickListener(v -> {
            cameraImageUri = createImageUri();
            if (cameraImageUri != null) {
                cameraLauncher.launch(cameraImageUri);
            }
            dialog.dismiss();
        });

        galleryButton.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
            dialog.dismiss();
        });

        dialog.show();
    }

    private Uri createImageUri() {
        File imageFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
        }

        if (imageFile != null) {
            return FileProvider.getUriForFile(this, "com.example.thepizzamaniaproject.fileprovider", imageFile);
        } else {
            return null;
        }
    }
}