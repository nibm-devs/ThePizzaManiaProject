package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.thepizzamaniaproject.Adapter.PizzaAdapter;
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import android.view.ViewTreeObserver;

public class ProductsActivity extends AppCompatActivity {

    private static final String TAG = "ProductsActivity";
    private ScrollView scrollView;
    private String selectedCategory;

    // Firebase
    private DatabaseReference pizzasRef;

    // RecyclerViews and Adapters
    private RecyclerView newArrivalsRecyclerView, delightRecyclerView, classicRecyclerView;
    private RecyclerView signatureRecyclerView, favouritesRecyclerView, supremeRecyclerView;
    private PizzaAdapter newArrivalsAdapter, delightAdapter, classicAdapter;
    private PizzaAdapter signatureAdapter, favouritesAdapter, supremeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        Log.d(TAG, "Activity created");

        Log.d(TAG, "setContentView completed");

        // Add logs after each major operation to see where it fails
        Log.d(TAG, "Starting initialization...");

        try {

            // Initialize Firebase reference with error handling
            try {
                pizzasRef = FirebaseDatabase.getInstance().getReference("pizzas");
                Log.d(TAG, "Firebase initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "Firebase initialization failed: " + e.getMessage());
                Toast.makeText(this, "Database connection failed", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Get the selected category from intent
            selectedCategory = getIntent().getStringExtra("SELECTED_CATEGORY");
            Log.d(TAG, "Selected category: " + selectedCategory);

            scrollView = findViewById(R.id.scrollView);

            // Initialize RecyclerViews
            initializeRecyclerViews();

            // Load data from Firebase
            loadPizzasFromFirebase();

            // Setup bottom navigation
            setupBottomNavigation();

            // Scroll to category after layout is drawn
            scrollView.post(() -> {
                scrollToCategory(selectedCategory);
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "App error occurred", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeRecyclerViews() {
        Log.d(TAG, "Starting RecyclerView initialization");

        try {
            // Initialize only the RecyclerViews that exist in the layout
            newArrivalsRecyclerView = findViewById(R.id.newArrivalsRecyclerView);
            delightRecyclerView = findViewById(R.id.delightRecyclerView);
            classicRecyclerView = findViewById(R.id.classicRecyclerView);
            signatureRecyclerView = findViewById(R.id.signatureRecyclerView);
            favouritesRecyclerView = findViewById(R.id.favouritesRecyclerView);
            supremeRecyclerView = findViewById(R.id.supremeRecyclerView);

            // Initialize adapters only for existing RecyclerViews
            if (newArrivalsRecyclerView != null) {
                newArrivalsRecyclerView.setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
                newArrivalsAdapter = new PizzaAdapter(new ArrayList<>());
                newArrivalsRecyclerView.setAdapter(newArrivalsAdapter);
                Log.d(TAG, "New Arrivals RecyclerView initialized");
            }

            if (delightRecyclerView != null) {
                delightRecyclerView.setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
                delightAdapter = new PizzaAdapter(new ArrayList<>());
                delightRecyclerView.setAdapter(delightAdapter);
                Log.d(TAG, "Delight RecyclerView initialized");
            }

            if (classicRecyclerView != null) {
                classicRecyclerView.setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
                classicAdapter = new PizzaAdapter(new ArrayList<>());
                classicRecyclerView.setAdapter(classicAdapter);
                Log.d(TAG, "Classic RecyclerView initialized");
            }

            if (signatureRecyclerView != null) {
                signatureRecyclerView.setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
                signatureAdapter = new PizzaAdapter(new ArrayList<>());
                signatureRecyclerView.setAdapter(signatureAdapter);
                Log.d(TAG, "Signature RecyclerView initialized");
            }

            if (favouritesRecyclerView != null) {
                favouritesRecyclerView.setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
                favouritesAdapter = new PizzaAdapter(new ArrayList<>());
                favouritesRecyclerView.setAdapter(favouritesAdapter);
                Log.d(TAG, "Favourites RecyclerView initialized");
            }

            if (supremeRecyclerView != null) {
                supremeRecyclerView.setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
                supremeAdapter = new PizzaAdapter(new ArrayList<>());
                supremeRecyclerView.setAdapter(supremeAdapter);
                Log.d(TAG, "Supreme RecyclerView initialized");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error initializing RecyclerViews: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPizzasFromFirebase() {
        if (pizzasRef == null) {
            Log.e(TAG, "Firebase reference is null");
            return;
        }

        // Only load for adapters that are initialized (not null)
        if (newArrivalsAdapter != null) {
            loadPizzasByCategory("new_arrivals", newArrivalsAdapter);
        }
        else {
            Log.d(TAG, "newArrivalsAdapter is null, skipping");
        }

        if (delightAdapter != null) {
            loadPizzasByCategory("delight", delightAdapter);
        } else {
            Log.d(TAG, "delightAdapter is null, skipping");
        }

        if (classicAdapter != null) {
            loadPizzasByCategory("classic", classicAdapter);
        } else {
            Log.d(TAG, "classicAdapter is null, skipping");
        }

        if (signatureAdapter != null) {
            loadPizzasByCategory("signature", signatureAdapter);
        } else {
            Log.d(TAG, "signatureAdapter is null, skipping");
        }

        if (favouritesAdapter != null) {
            loadPizzasByCategory("favourites", favouritesAdapter);
        } else {
            Log.d(TAG, "favouritesAdapter is null, skipping");
        }

        if (supremeAdapter != null) {
            loadPizzasByCategory("supreme", supremeAdapter);
        } else {
            Log.d(TAG, "supremeAdapter is null, skipping");
        }
    }

    private void loadPizzasByCategory(String category, PizzaAdapter adapter) {
        if (pizzasRef == null) {
            Log.e(TAG, "Cannot load category " + category + ", Firebase ref is null");
            return;
        }

        // Add null check for adapter
        if (adapter == null) {
            Log.e(TAG, "Cannot load category " + category + ", adapter is null");
            return;
        }

        Query query = pizzasRef.orderByChild("category").equalTo(category);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    List<PizzaDomain> pizzaList = new ArrayList<>();

                    if (!snapshot.exists()) {
                        Log.d(TAG, "No data found for category: " + category);
                        return;
                    }

                    Log.d(TAG, "Found " + snapshot.getChildrenCount() + " items for category: " + category);

                    for (DataSnapshot pizzaSnapshot : snapshot.getChildren()) {
                        PizzaDomain pizza = pizzaSnapshot.getValue(PizzaDomain.class);
                        if (pizza != null) {
                            Log.d(TAG, "Adding pizza: " + pizza.getTitle() + " to category: " + category);
                            pizzaList.add(pizza);
                        }
                    }

                    adapter.updatePizzas(pizzaList);
                    Log.d(TAG, "Loaded " + pizzaList.size() + " pizzas for: " + category);

                } catch (Exception e) {
                    Log.e(TAG, "Error processing data for " + category + ": " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error for " + category + ": " + error.getMessage());
            }
        });
    }

    private void scrollToCategory(String category)
    {
        if (category == null)
        {
            Log.d(TAG, "No category specified for scrolling");
            return;
        }

        try
        {
            final View targetView;

            // Map the category names from HomeActivity to the correct titles
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
                case "Favourites": // This is what comes from HomeActivity
                    targetView = findViewById(R.id.favouritesTitle);
                    break;
                case "Supreme":
                    targetView = findViewById(R.id.supremeTitle);
                    break;
                default:
                    targetView = null;
                    Log.d(TAG, "Unknown category: " + category);
                    break;
            }

//            if (targetView != null)
//            {
//                targetView.post(() -> {
//                    try {
//                        scrollView.smoothScrollTo(0, targetView.getTop());
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error scrolling to category: " + e.getMessage());
//                    }
//                });
//            }


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
        catch (Exception e)
        {
            Log.e(TAG, "Error in scrollToCategory: " + e.getMessage());
        }
    }

    private void setupBottomNavigation() {
        try
        {
            findViewById(R.id.homeBtn).setOnClickListener(v -> {
                Intent intent = new Intent(ProductsActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            });

            findViewById(R.id.profileBtn).setOnClickListener(v -> {
                startActivity(new Intent(ProductsActivity.this, ProfileActivity.class));
            });

            findViewById(R.id.cartBtn).setOnClickListener(v -> {
                startActivity(new Intent(ProductsActivity.this, CartActivity.class));
            });

            findViewById(R.id.branchesBtn).setOnClickListener(v -> {
                startActivity(new Intent(ProductsActivity.this, BranchesActivity.class));
            });

        }
        catch (Exception e)
        {
            Log.e(TAG, "Error setting up bottom navigation: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }
}
