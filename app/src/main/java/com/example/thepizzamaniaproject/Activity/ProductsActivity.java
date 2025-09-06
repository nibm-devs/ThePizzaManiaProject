package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.util.Log;
import android.view.ViewTreeObserver;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thepizzamaniaproject.R;

public class ProductsActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        // Get the selected category from intent
        selectedCategory = getIntent().getStringExtra("SELECTED_CATEGORY");

        scrollView = findViewById(R.id.scrollView);


        // Initialize your RecyclerViews and data here

        // After loading data, scroll to the selected category
        scrollView.post(new Runnable() {
            @Override
            public void run()
            {
                scrollToCategory(selectedCategory);
            }
        });
        


        // Home button
        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to Home
                Intent intent = new Intent(ProductsActivity.this, HomeActivity.class);
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
                Intent intent = new Intent(ProductsActivity.this, ProfileActivity.class);
                startActivity(intent);

            }
        });


        // Cart button
        findViewById(R.id.cartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Navigate to cart
                Intent intent = new Intent(ProductsActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        // Settings button
//        findViewById(R.id.settingsBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to settings
//                Intent intent = new Intent(ProductsActivity.this, SettingsActivity.class);
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
                Intent intent = new Intent(ProductsActivity.this,BranchesActivity.class);
                startActivity(intent);
            }
        });

        
    }





    // Scroll to the selected category
    private void scrollToCategory(String category)
    {
        if (category == null) return;

        final View targetView;

        switch (category)
        {
            case "New":
                targetView = findViewById(R.id.newArrivalsTitle);
                break;
            case "DELIGHT":
                targetView = findViewById(R.id.delightTitle);
                break;
            case "Classic":
                targetView = findViewById(R.id.classicTitle);
                break;
            case "Signature":
                targetView = findViewById(R.id.signatureTitle);
                break;
            case "Favourites":
                targetView = findViewById(R.id.favouritesTitle);
                break;
            case "Supreme":
                targetView = findViewById(R.id.supremeTitle);
                break;
            default:
                targetView = null;
                break;
        }

        if (targetView != null)
        {
            // Use post to ensure the view is fully laid out
            targetView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    // Get the position of the target view
                    int[] location = new int[2];
                    targetView.getLocationOnScreen(location);
                    int y = location[1];

                    // Scroll to the position
                    scrollView.smoothScrollTo(0, y);
                }
            });
        }
    }


}