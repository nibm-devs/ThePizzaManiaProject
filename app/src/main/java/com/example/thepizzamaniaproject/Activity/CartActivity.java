package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thepizzamaniaproject.Adapter.CartAdapter;
import com.example.thepizzamaniaproject.Helper.CartManager;
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.R;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    // Declare all variables at class level
    private TextView itemsTotalTextView, taxTextView, deliveryTextView, totalTextView;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        // Initialize views
        itemsTotalTextView = findViewById(R.id.itemsTotalTextView);
        taxTextView = findViewById(R.id.taxTextView);
        deliveryTextView = findViewById(R.id.deliveryTextView);
        totalTextView = findViewById(R.id.totalTextView);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get cart items
        List<PizzaDomain> cartItems = CartManager.getInstance().getCartItems();
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(cartAdapter);

        // Update totals
        updateCartTotals();

        // Get cart items and setup adapter
        setupCartAdapter();


        // Highlight current page
        highlightCurrentPage();


        // Home button
        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to Home
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
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
                Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
                startActivity(intent);

            }
        });


        // Cart button
        findViewById(R.id.cartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Navigate to cart
                highlightCurrentPage();
            }
        });


        // Settings button
//        findViewById(R.id.settingsBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to settings
//                Intent intent = new Intent(CartActivity.this, SettingsActivity.class);
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
                Intent intent = new Intent(CartActivity.this,BranchesActivity.class);
                startActivity(intent);
            }
        });


    }


    private void setupCartAdapter() {
        List<PizzaDomain> cartItems = CartManager.getInstance().getCartItems();
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(cartAdapter);
    }


    public void updateCartTotals() {
        List<PizzaDomain> cartItems = CartManager.getInstance().getCartItems();
        double itemsTotal = 0;

        for (PizzaDomain pizza : cartItems) {
            itemsTotal += pizza.getPrice() * pizza.getQuantity();
        }

        double tax = itemsTotal * 0.1; // 10% tax
        double delivery = itemsTotal > 0 ? 350 : 0; // Rs.350 delivery charge if items exist
        double total = itemsTotal + tax + delivery;

        itemsTotalTextView.setText("Rs." + String.format("%.2f", itemsTotal));
        taxTextView.setText("Rs." + String.format("%.2f", tax));
        deliveryTextView.setText("Rs." + String.format("%.2f", delivery));
        totalTextView.setText("Rs." + String.format("%.2f", total));
    }


    // To highlight the current page text
    private void highlightCurrentPage()
    {

        View cartBtn = findViewById(R.id.cartBtn);
        TextView cartText = cartBtn.findViewById(R.id.cartText);


        cartText.setTextColor(Color.parseColor("#FF3D00"));

    }


    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the cart when returning to this activity
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
            updateCartTotals();
        }
    }


}