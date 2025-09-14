package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener, RecommendedAdapter.OnItemClickListener {

    private static final String TAG = "HomeActivity";
    private CategoryAdapter adapter;
    private RecommendedAdapter adapter2;
    private RecyclerView recyclerViewCategoryList, recyclerViewRecommendedList;
    private ImageView profileImage;
    private TextView userNameText;

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private UserDomain currentUser;

    // Firebase reference
    private DatabaseReference pizzasRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Initialize the "See more" TextView
        TextView seeMoreText = findViewById(R.id.txtseemore);

        // Initialize Firebase
        pizzasRef = FirebaseDatabase.getInstance().getReference("pizzas");

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

        // Initialize views
        profileImage = findViewById(R.id.imageView3);
        userNameText = findViewById(R.id.textView6); // The "Hi! Name" text

        // Load user data to home page
        loadUserData();

        recyclerViewCategory();
        loadRecommendedPizzas(); // Replaced recyclerViewPopuler()

        // Initialize profile image
        profileImage = findViewById(R.id.imageView3);

        // Set click listener for profile image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfileActivity();
            }
        });

        // Highlight current page
        highlightCurrentPage();

        // Home button
        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightCurrentPage();
            }
        });

        // Profile button
        findViewById(R.id.profileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to profile
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Cart button
        findViewById(R.id.cartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to cart
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Branches button
        findViewById(R.id.branchesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to branches
                Intent intent = new Intent(HomeActivity.this, BranchesActivity.class);
                startActivity(intent);
            }
        });

        // click "See more" text
        seeMoreText.setOnClickListener(new View.OnClickListener() {
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
    private void loadCurrentUser() {
        String userEmail = sessionManager.getUserEmail();
        if (userEmail != null) {
            currentUser = databaseHelper.getUserByEmail(userEmail);

            if (currentUser == null) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                sessionManager.logoutUser();
                goToLoginActivity();
            }
        } else {
            goToLoginActivity();
        }
    }

    // Load user data to home page
    private void loadUserData() {
        if (currentUser != null) {
            // Get first name only
            String fullName = currentUser.getName();
            String firstName = fullName.split(" ")[0]; // Gets first word

            userNameText.setText("Hi! " + firstName);

            // Load profile image if exists
            if (currentUser.getProfileImage() != null && !currentUser.getProfileImage().isEmpty()) {
                try {
                    profileImage.setImageURI(Uri.parse(currentUser.getProfileImage()));
                } catch (Exception e) {
                    // Use default image if error
                    profileImage.setImageResource(R.drawable.profile);
                }
            } else {
                // Set default image if no image exists
                profileImage.setImageResource(R.drawable.profile);
            }
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // when profile pic click go to profile interface
    private void goToProfileActivity() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    // To highlight the current page text
    private void highlightCurrentPage() {
        View homeBtn = findViewById(R.id.homeBtn);
        TextView homeText = homeBtn.findViewById(R.id.homeText);

        homeText.setTextColor(Color.parseColor("#FF3D00"));
    }

    private void recyclerViewCategory() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoryList = findViewById(R.id.view1);
        recyclerViewCategoryList.setLayoutManager(linearLayoutManager);

        ArrayList<CategoryDomain> categoryList = new ArrayList<>();
        categoryList.add(new CategoryDomain("New", "cat_1"));
        categoryList.add(new CategoryDomain("DELIGHT", "cat_1"));
        categoryList.add(new CategoryDomain("Classic", "cat_1"));
        categoryList.add(new CategoryDomain("Signature", "cat_1"));
        categoryList.add(new CategoryDomain("Favourites", "cat_1"));
        categoryList.add(new CategoryDomain("Supreme", "cat_1"));

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

    // Load recommended pizzas from Firebase
    private void loadRecommendedPizzas() {
        pizzasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PizzaDomain> allPizzas = new ArrayList<>();

                for (DataSnapshot pizzaSnapshot : snapshot.getChildren()) {
                    PizzaDomain pizza = pizzaSnapshot.getValue(PizzaDomain.class);
                    if (pizza != null) {
                        allPizzas.add(pizza);
                    }
                }

                // Select 3 random pizzas for recommendations
                List<PizzaDomain> recommendedPizzas = getRandomPizzas(allPizzas, 3);

                // Initialize the recommended RecyclerView
                initializeRecommendedRecyclerView(recommendedPizzas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load pizzas: " + error.getMessage());
                // Fallback to hardcoded pizzas if Firebase fails
                recyclerViewPopulerFallback();
            }
        });
    }

    private List<PizzaDomain> getRandomPizzas(List<PizzaDomain> allPizzas, int count) {
        if (allPizzas.size() <= count) {
            return allPizzas;
        }

        List<PizzaDomain> randomPizzas = new ArrayList<>();
        List<PizzaDomain> tempList = new ArrayList<>(allPizzas);
        Collections.shuffle(tempList, new Random());

        for (int i = 0; i < count; i++) {
            randomPizzas.add(tempList.get(i));
        }

        return randomPizzas;
    }

    private void initializeRecommendedRecyclerView(List<PizzaDomain> pizzaList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecommendedList = findViewById(R.id.view2);
        recyclerViewRecommendedList.setLayoutManager(linearLayoutManager);

        adapter2 = new RecommendedAdapter(pizzaList);
        adapter2.setOnItemClickListener(this); // Set the click listener
        recyclerViewRecommendedList.setAdapter(adapter2);
    }

    private void recyclerViewPopulerFallback() {
        // Fallback to hardcoded pizzas if Firebase fails
        ArrayList<PizzaDomain> pizzaList = new ArrayList<>();
        pizzaList.add(new PizzaDomain("Margherita", "https://i.postimg.cc/bDn9p2Lp/DELIGHT1.png", "Rich tomato sauce base topped with cream cheese, tomato, mozzarella & basil leaves", 2980.00, 4.8, 20, "delight",1));
        pizzaList.add(new PizzaDomain("Chilli Chicken Pizza", "https://i.postimg.cc/V0cB6LHM/DELIGHT2.png", "A pizza topped with Spicy Chicken, Green Chillies, Onions & Mozzarella", 2900.00, 4.4, 20, "delight",1));
        pizzaList.add(new PizzaDomain("Sausage Delight", "https://i.postimg.cc/yJtyMTmL/DELIGHT3.png", "Chicken sausages & onions with a double layer of cheese.", 2950.00, 4.8, 20, "delight",1));

        initializeRecommendedRecyclerView(pizzaList);
    }

    // Implement the OnItemClickListener interface
    @Override
    public void onItemClick(PizzaDomain pizza) {
        // Open pizza details activity
        Intent intent = new Intent(HomeActivity.this, PizzaDetailActivity.class);
        intent.putExtra("pizza", pizza);
        startActivity(intent);

        // Optional: Add animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}

