package com.example.thepizzamaniaproject.Activity;




import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thepizzamaniaproject.R;
import com.example.thepizzamaniaproject.Domain.MenuItem;
import com.example.thepizzamaniaproject.Adapter.MenuAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMenuActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddItem;
    private TextView tvMenuCount, tvActiveOrders;

    private MenuAdapter menuAdapter;
    private List<MenuItem> menuItemList;

    private FirebaseFirestore db;
    private static final String COLLECTION_PIZZAS = "pizzas";
    private static final String COLLECTION_ORDERS = "orders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initViews();

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pizza Mania");
            getSupportActionBar().setSubtitle("Admin Panel");
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Load menu items
        loadMenuItems();

        // Load active orders count
        loadActiveOrdersCount();

        // Set click listeners
        fabAddItem.setOnClickListener(v -> {
            // Open add item activity/dialog
            openAddItemDialog();
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        fabAddItem = findViewById(R.id.fabAddItem);
        tvMenuCount = findViewById(R.id.tvMenuCount);
        tvActiveOrders = findViewById(R.id.tvActiveOrders);
    }

    private void setupRecyclerView() {
        menuItemList = new ArrayList<>();
        menuAdapter = new MenuAdapter(menuItemList, this, true); // true for admin mode

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(menuAdapter);

        // Set item click listener for admin actions
        menuAdapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, MenuItem menuItem) {
                // View item details
                showItemDetails(menuItem);
            }

            @Override
            public void onEditClick(int position, MenuItem menuItem) {
                // Edit item
                openEditItemDialog(position, menuItem);
            }

            @Override
            public void onDeleteClick(int position, MenuItem menuItem) {
                // Delete item
                deleteMenuItem(position, menuItem);
            }

            @Override
            public void onToggleStatusClick(int position, MenuItem menuItem) {
                // Toggle availability
                toggleItemAvailability(position, menuItem);
            }
        });
    }

    private void loadMenuItems() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection(COLLECTION_PIZZAS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            menuItemList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MenuItem menuItem = document.toObject(MenuItem.class);
                                menuItem.setId(document.getId());
                                menuItemList.add(menuItem);
                            }

                            // Update menu count
                            tvMenuCount.setText(String.valueOf(menuItemList.size()));

                            menuAdapter.updateData(menuItemList);
                        } else {
                            Toast.makeText(AdminMenuActivity.this,
                                    "Error loading menu: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadActiveOrdersCount() {
        db.collection(COLLECTION_ORDERS)
                .whereEqualTo("status", "active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int activeOrders = task.getResult().size();
                            tvActiveOrders.setText(String.valueOf(activeOrders));
                        } else {
                            tvActiveOrders.setText("0");
                            Toast.makeText(AdminMenuActivity.this,
                                    "Error loading orders: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openAddItemDialog() {
        // Implement dialog to add new pizza item
        Toast.makeText(this, "Open add pizza dialog", Toast.LENGTH_SHORT).show();
        // You can implement an AddPizzaDialogFragment here
    }

    private void showItemDetails(MenuItem menuItem) {
        // Implement pizza details view
        Toast.makeText(this, "View details: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        // You can implement a PizzaDetailsDialogFragment here
    }

    private void openEditItemDialog(int position, MenuItem menuItem) {
        // Implement dialog to edit pizza item
        Toast.makeText(this, "Edit pizza: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        // You can implement an EditPizzaDialogFragment here
    }

    private void deleteMenuItem(int position, MenuItem menuItem) {
        db.collection(COLLECTION_PIZZAS)
                .document(menuItem.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        menuAdapter.removeItem(position);
                        Toast.makeText(AdminMenuActivity.this,
                                "Pizza deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminMenuActivity.this,
                                "Error deleting pizza: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleItemAvailability(int position, MenuItem menuItem) {
        MenuItem updatedItem = new MenuItem(
                menuItem.getId(),
                menuItem.getTitle(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getPicture(),
                menuItem.getCategory(),
                menuItem.getStar(),
                menuItem.getTime(),
                !menuItem.isAvailable()
        );

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("title", updatedItem.getTitle());
        itemData.put("description", updatedItem.getDescription());
        itemData.put("price", updatedItem.getPrice());
        itemData.put("picture", updatedItem.getPicture());
        itemData.put("category", updatedItem.getCategory());
        itemData.put("star", updatedItem.getStar());
        itemData.put("time", updatedItem.getTime());
        itemData.put("available", updatedItem.isAvailable());

        db.collection(COLLECTION_PIZZAS)
                .document(updatedItem.getId())
                .update(itemData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        menuAdapter.updateItem(position, updatedItem);
                        String status = updatedItem.isAvailable() ? "available" : "unavailable";
                        Toast.makeText(AdminMenuActivity.this,
                                "Pizza marked as " + status, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminMenuActivity.this,
                                "Error updating pizza: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadMenuItems();
        loadActiveOrdersCount();
    }
}