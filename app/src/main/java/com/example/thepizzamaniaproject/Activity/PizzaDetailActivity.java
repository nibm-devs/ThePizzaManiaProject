package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thepizzamaniaproject.R;

public class PizzaDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pizza_detail);


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        // Home button
        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to Home
                Intent intent = new Intent(PizzaDetailActivity.this, HomeActivity.class);
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
                Intent intent = new Intent(PizzaDetailActivity.this, ProfileActivity.class);
                startActivity(intent);

            }
        });


        // Cart button
        findViewById(R.id.cartBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to cart
                Intent intent = new Intent(PizzaDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        // Settings button
//        findViewById(R.id.settingsBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to settings
//                Intent intent = new Intent(PizzaDetailActivity.this, SettingsActivity.class);
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
                Intent intent = new Intent(PizzaDetailActivity.this,BranchesActivity.class);
                startActivity(intent);
            }
        });






    }
}