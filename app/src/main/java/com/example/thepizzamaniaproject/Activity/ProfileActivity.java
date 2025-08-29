package com.example.thepizzamaniaproject.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thepizzamaniaproject.Domain.UserDomain;
import com.example.thepizzamaniaproject.Helper.DatabaseHelper;
import com.example.thepizzamaniaproject.Helper.SessionManager;
import com.example.thepizzamaniaproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProfileActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private UserDomain currentUser;

    // View variables
    private ImageView profileImage;
    private TextView profileName, profileEmail;
    private EditText editTextName, editTextEmail, editTextPhone, editTextAddress;
    private EditText currentPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private RadioGroup radioGroupGender;
    private Button updateButton, changePasswordButton, logoutButton;

    private Uri selectedImageUri;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        // Initialize helpers
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            goToLoginActivity();
            return;
        }

        // Load current user data
        loadCurrentUser();

        initializeViews();
        loadUserData();
        setupClickListeners();



        // Highlight current page
        highlightCurrentPage();


        // Home button
        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to Home
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                 startActivity(intent);
                 finish();
            }
        });


        // Profile button
        findViewById(R.id.profileBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                highlightCurrentPage();
                loadCurrentUser();
                loadUserData();
            }
        });


        // Cart button
//        findViewById(R.id.cartBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to cart
//                Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
//                startActivity(intent);
//
//            }
//        });


        // Settings button
//        findViewById(R.id.settingsBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to settings
//                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
//                startActivity(intent);
//
//            }
//        });


        // Branches button
        findViewById(R.id.branchesBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to branches
                Intent intent = new Intent(ProfileActivity.this,BranchesActivity.class);
                startActivity(intent);
            }
        });



    }



    // To highlight the current page text
    private void highlightCurrentPage()
    {

        View profileBtn = findViewById(R.id.profileBtn);
        TextView profileText = profileBtn.findViewById(R.id.profileText);


        profileText.setTextColor(Color.parseColor("#FF3D00"));

    }




    private void loadCurrentUser() {
        String userEmail = sessionManager.getUserEmail();
        currentUser = databaseHelper.getUserByEmail(userEmail);

        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
            goToLoginActivity();
        }
    }


    private void initializeViews()
    {
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        updateButton = findViewById(R.id.updateButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Password change views
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
    }


    private void loadUserData()
    {
        if (currentUser != null)
        {
            // Set header data
            profileName.setText(currentUser.getName());
            profileEmail.setText(currentUser.getEmail());

            // Set form data
            editTextName.setText(currentUser.getName());
            editTextEmail.setText(currentUser.getEmail());
            editTextPhone.setText(currentUser.getPhone());
            editTextAddress.setText(currentUser.getAddress());


            // Set gender
            if (currentUser.getGender() != null)
            {
                if (currentUser.getGender().equalsIgnoreCase("male"))
                {
                    radioGroupGender.check(R.id.radioMale);
                }
                else if (currentUser.getGender().equalsIgnoreCase("female"))
                {
                    radioGroupGender.check(R.id.radioFemale);
                }
            }

            // Load profile image if exists
            if (currentUser.getProfileImage() != null && !currentUser.getProfileImage().isEmpty())
            {
                try
                {
                    profileImage.setImageURI(Uri.parse(currentUser.getProfileImage()));
                }
                catch (Exception e)
                {
                    // Use default image if error
                    profileImage.setImageResource(R.drawable.profile);
                }
            }
        }

    }


    private void setupClickListeners()
    {
        // Edit button
        findViewById(R.id.editImageButton).setOnClickListener(v -> showPhotoChoiceDialog());

        // Update button
        updateButton.setOnClickListener(v -> updateProfile());

        // Change password button
        changePasswordButton.setOnClickListener(v -> changePassword());

        // Logout button
        logoutButton.setOnClickListener(v -> logoutUser());
    }






    // Shows the choice dialog
    private void showPhotoChoiceDialog()
    {
        View dialogView = getLayoutInflater().inflate(R.layout.photo_choice_dialog, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        dialog.show();

        // Camera option
        dialogView.findViewById(R.id.cameraOption).setOnClickListener(v -> {
            dialog.dismiss();
            checkCameraPermission();
        });

        // Gallery option
        dialogView.findViewById(R.id.galleryOption).setOnClickListener(v -> {
            dialog.dismiss();
            openImagePicker();
        });

        // Cancel button
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());
    }


    private void openImagePicker()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    // To check and request camera permission
    private void checkCameraPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION);
        }
        else
        {
            dispatchTakePictureIntent();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }
            else
            {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void dispatchTakePictureIntent()
    {
        try
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null)
            {
                File photoFile = null;
                try
                {
                    photoFile = createImageFile();
                }
                catch (IOException ex)
                {
                    Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (photoFile != null)
                {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.thepizzamaniaproject.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
            else
            {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error opening camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);

            // Save image URI to user object
            if (currentUser != null && selectedImageUri != null)
            {
                currentUser.setProfileImage(selectedImageUri.toString());
            }
        }
    }





    // Update profile
    private void updateProfile()
    {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String gender = getSelectedGender();

        // Validation
        if (name.isEmpty())
        {
            editTextName.setError("Please enter your name");
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please enter a valid email");
            return;
        }

        // Update user object
        if (currentUser != null)
        {
            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            currentUser.setGender(gender);

            // Update in database
            boolean success = databaseHelper.updateUser(currentUser);

            if (success)
            {
                // Update header
                profileName.setText(name);
                profileEmail.setText(email);

                // Update session if email changed
                if (!email.equals(sessionManager.getUserEmail()))
                {
                    sessionManager.createLoginSession(email, name, currentUser.getId());
                }

                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void changePassword()
    {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmNewPasswordEditText.getText().toString().trim();

        // Validation
        if (currentPassword.isEmpty())
        {
            currentPasswordEditText.setError("Enter current password");
            return;
        }

        if (newPassword.isEmpty() || newPassword.length() < 6)
        {
            newPasswordEditText.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword))
        {
            confirmNewPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Verify current password
        String userEmail = sessionManager.getUserEmail();
        if (databaseHelper.checkUser(userEmail, currentPassword))
        {
            // Update password in database
            if (databaseHelper.updatePassword(userEmail, newPassword))
            {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                clearPasswordFields();
            }
            else
            {
                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            currentPasswordEditText.setError("Incorrect current password");
        }
    }

    private void logoutUser()
    {
        sessionManager.logoutUser();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        goToLoginActivity();
    }

    private String getSelectedGender()
    {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();

        if (selectedId == R.id.radioMale)
        {
            return "Male";
        }
        else if (selectedId == R.id.radioFemale)
        {
            return "Female";
        }
        return "";
    }


    private void clearPasswordFields()
    {
        currentPasswordEditText.setText("");
        newPasswordEditText.setText("");
        confirmNewPasswordEditText.setText("");
    }

    private void goToLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }




}