package com.example.thepizzamaniaproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thepizzamaniaproject.Adapter.MenuAdapter;
import com.example.thepizzamaniaproject.Domain.MenuItem;
import com.example.thepizzamaniaproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminMenuActivity extends AppCompatActivity implements MenuAdapter.MenuItemClickListener {

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<MenuItem> menuItems;
    private FloatingActionButton fabAddItem;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        fabAddItem = findViewById(R.id.fabAddItem);
        progressBar = findViewById(R.id.progressBar);

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Pizza Mania - Admin Panel");

        // Initialize menu items list
        menuItems = new ArrayList<>();

        // Setup RecyclerView
        adapter = new MenuAdapter(menuItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load menu items from Firebase
        loadMenuItemsFromFirebase();

        // Set click listeners
        fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, AddEditMenuItemActivity.class);
            startActivity(intent);
        });
    }

    private void loadMenuItemsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("menuItems")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        menuItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MenuItem item = document.toObject(MenuItem.class);
                            item.setId(document.getId()); // Set the document ID
                            menuItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading menu items: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(int position) {
        MenuItem menuItem = menuItems.get(position);
        Intent intent = new Intent(this, AddEditMenuItemActivity.class);
        intent.putExtra("menuItem", menuItem);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        MenuItem menuItem = menuItems.get(position);
        deleteMenuItemFromFirebase(menuItem.getId(), position);
    }

    private void deleteMenuItemFromFirebase(String itemId, int position) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("menuItems").document(itemId)
                .delete()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        menuItems.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error deleting item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from Add/Edit activity
        loadMenuItemsFromFirebase();
    }
}