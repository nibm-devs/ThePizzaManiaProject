package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thepizzamaniaproject.Adapter.CategoryAdapter;
import com.example.thepizzamaniaproject.Adapter.RecommendedAdapter;
import com.example.thepizzamaniaproject.Domain.CategoryDomain;
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.Domain.UserDomain;
import com.example.thepizzamaniaproject.Helper.DatabaseHelper;
import com.example.thepizzamaniaproject.Helper.SessionManager;
import com.example.thepizzamaniaproject.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

//    private RecyclerView.Adapter adapter,adapter2;
    private CategoryAdapter adapter;
    private RecyclerView.Adapter adapter2;
    private RecyclerView recyclerViewCategoryList, recyclerViewRecommendedList;
    private ImageView profileImage;
    private TextView userNameText;

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private UserDomain currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//
//
//        });


        // Initialize the "See more" TextView
        TextView seeMoreText = findViewById(R.id.txtseemore);


        // Initialize helpers
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);


        // Check if user is logged in
        if (!sessionManager.isLoggedIn())
        {
            goToLoginActivity();
            return;
        }


        // Load current user data
        loadCurrentUser();


        // Initialize views
        profileImage = findViewById(R.id.imageView3);
        userNameText = findViewById(R.id.textView6); // The "Hi! Name" text


        // Load user data to home page
        loadUserData();


        recyclerViewCategory();
        recyclerViewPopuler();


        // Initialize profile image
        profileImage = findViewById(R.id.imageView3);

        // Set click listener for profile image
        profileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity();
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
                highlightCurrentPage();
            }
        });


        // Profile button
        findViewById(R.id.profileBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to profile
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
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
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);

            }
        });


        // Settings button
//        findViewById(R.id.settingsBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to settings
//                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
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
                Intent intent = new Intent(HomeActivity.this,BranchesActivity.class);
                startActivity(intent);
            }
        });



        // click "See more" text
        seeMoreText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Navigate to ProductActivity
                Intent intent = new Intent(HomeActivity.this, ProductsActivity.class);
                startActivity(intent);

                // Optional: Add a slide animation
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }


    // Load current user data
    private void loadCurrentUser()
    {
        String userEmail = sessionManager.getUserEmail();
        if (userEmail != null)
        {
            currentUser = databaseHelper.getUserByEmail(userEmail);

            if (currentUser == null)
            {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                sessionManager.logoutUser();
                goToLoginActivity();
            }
        }
        else
        {
            goToLoginActivity();
        }
    }


    // Load user data to home page
    private void loadUserData()
    {
        if (currentUser != null)
        {
            // Get first name only
            String fullName = currentUser.getName();
            String firstName = fullName.split(" ")[0]; // Gets first word

            userNameText.setText("Hi! " + firstName);

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
            else
            {
                // Set default image if no image exists
                profileImage.setImageResource(R.drawable.profile);
            }
        }
    }



    private void goToLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // when profile pic click go to profile interface
    private void goToProfileActivity()
    {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }



    // To highlight the current page text
    private void highlightCurrentPage()
    {

        View homeBtn = findViewById(R.id.homeBtn);
        TextView homeText = homeBtn.findViewById(R.id.homeText);

        homeText.setTextColor(Color.parseColor("#FF3D00"));

    }



    private void recyclerViewCategory()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewCategoryList = findViewById(R.id.view1);
        recyclerViewCategoryList.setLayoutManager(linearLayoutManager);

        ArrayList<CategoryDomain> categoryList = new ArrayList<>();
        categoryList.add(new CategoryDomain("New","cat_1"));
        categoryList.add(new CategoryDomain("DELIGHT","cat_1"));
        categoryList.add(new CategoryDomain("Classic","cat_1"));
        categoryList.add(new CategoryDomain("Signature","cat_1"));
        categoryList.add(new CategoryDomain("Favourites","cat_1"));
        categoryList.add(new CategoryDomain("Supreme","cat_1"));


        adapter = new CategoryAdapter(categoryList);
        adapter.setOnCategoryClickListener(this); // Set the click listener
        recyclerViewCategoryList.setAdapter(adapter);

    }


    @Override
    public void onCategoryClick(String categoryName) {
        // Navigate to ProductsActivity with the selected category
        Intent intent = new Intent(HomeActivity.this, ProductsActivity.class);
        intent.putExtra("SELECTED_CATEGORY", categoryName);
        startActivity(intent);

        // Optional: Add animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    private void recyclerViewPopuler()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewRecommendedList = findViewById(R.id.view2);
        recyclerViewRecommendedList.setLayoutManager(linearLayoutManager);

        ArrayList<PizzaDomain> pizzaList = new ArrayList<>();
        pizzaList.add(new PizzaDomain("Margherita","pizza_recommended01","A hugely popular margherita, with a deliciously tangy single cheese topping",3200.00,5,20));
        pizzaList.add(new PizzaDomain("Pepperoni Pizza","pizza_recommended02","A hugely popular margherita, with a deliciously tangy single cheese topping",3800.00,4,18));
        pizzaList.add(new PizzaDomain("Cheese Pizza","pizza_recommended03","A hugely popular margherita, with a deliciously tangy single cheese topping",3600.00,3,16));


        adapter2 = new RecommendedAdapter(pizzaList);
        recyclerViewRecommendedList.setAdapter(adapter2);

    }






}