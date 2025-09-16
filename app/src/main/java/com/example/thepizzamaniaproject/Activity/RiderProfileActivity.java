package com.example.thepizzamaniaproject.Activity;

import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RiderProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText editTextName, editTextEmail, editTextPhone, editTextAddress;
    private TextView profileName;

    private DatabaseReference riderRef;

    private Uri imageUri;

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
            riderRef = FirebaseDatabase.getInstance().getReference().child("Riders").child(currentUserId);
            loadRiderData();
        } else {
            // Handle user not logged in
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        profileImage.setImageURI(imageUri);
                        uploadToCloudinary();
                    }
                });

        editImageButton.setOnClickListener(v -> mGetContent.launch("image/*"));

        saveButton.setOnClickListener(v -> saveRiderInformation());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadRiderData() {
        riderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String image = snapshot.child("image").getValue(String.class);

                    editTextName.setText(name);
                    profileName.setText(name);
                    editTextEmail.setText(email);
                    editTextPhone.setText(phone);
                    editTextAddress.setText(address);

                    if (image != null && !image.isEmpty()) {
                        Picasso.get().load(image).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RiderProfileActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToCloudinary() {
        if (imageUri != null) {
            MediaManager.get().upload(imageUri).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    // Optionally handle start of upload
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    // Optionally handle progress
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String imageUrl = (String) resultData.get("url");
                    updateProfileImage(Objects.requireNonNull(imageUrl));
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Toast.makeText(RiderProfileActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    // Optionally handle reschedule
                }
            }).dispatch();
        }
    }

    private void updateProfileImage(String imageUrl) {
        riderRef.child("image").setValue(imageUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RiderProfileActivity.this, "Profile image updated.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RiderProfileActivity.this, "Failed to update image.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRiderInformation() {
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

        riderRef.updateChildren(riderMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RiderProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RiderProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}