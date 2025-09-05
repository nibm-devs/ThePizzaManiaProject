package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thepizzamaniaproject.R;

public class BranchesActivity extends AppCompatActivity {

    // Coordinates for branches (example coordinates for Colombo and Galle)
    private static final double COLOMBO_LATITUDE = 6.9271;
    private static final double COLOMBO_LONGITUDE = 79.8612;
    private static final double GALLE_LATITUDE = 6.0535;
    private static final double GALLE_LONGITUDE = 80.2210;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_branches);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        // buttons
        Button colomboFindButton = findViewById(R.id.colomboFindButton);
        Button galleFindButton = findViewById(R.id.galleFindButton);



        // on click
        colomboFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap(COLOMBO_LATITUDE, COLOMBO_LONGITUDE, "Pizza Mania - Colombo Branch");
            }
        });

        galleFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap(GALLE_LATITUDE, GALLE_LONGITUDE, "Pizza Mania - Galle Branch");
            }
        });


        // Highlight current page
        highlightCurrentPage();


        // Home button
        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to home - finish current activity to go back
                //finish();

                // or start a new HomeActivity:
                 Intent intent = new Intent(BranchesActivity.this, HomeActivity.class);
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
                // Navigate to profile
                Intent intent = new Intent(BranchesActivity.this, ProfileActivity.class);
                startActivity(intent);

            }
        });


        // Cart button
        findViewById(R.id.cartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Navigate to cart
                Intent intent = new Intent(BranchesActivity.this, CartActivity.class);
                startActivity(intent);

            }
        });


        // Settings button
//        findViewById(R.id.settingsBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to settings
//                Intent intent = new Intent(BranchesActivity.this, SettingsActivity.class);
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
                highlightCurrentPage();
            }
        });

    }



    // To highlight the current page text
    private void highlightCurrentPage()
    {

        View branchesBtn = findViewById(R.id.branchesBtn);
        TextView branchesText = branchesBtn.findViewById(R.id.branchesText);


        branchesText.setTextColor(Color.parseColor("#FF3D00"));

    }


    private void openMap(double latitude, double longitude, String label)
    {

        // Create a URI for the location
        String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + label + ")";

        // Create an intent to open the map
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps"); // Specify package to use Google Maps

        // Check if Google Maps is installed
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else
        {
            // Fallback: Open in any available map application
            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?q=" + latitude + "," + longitude + "(" + label + ")"));
            startActivity(fallbackIntent);
        }
    }



}

