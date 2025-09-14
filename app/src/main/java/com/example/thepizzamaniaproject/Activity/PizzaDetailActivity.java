package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.thepizzamaniaproject.Helper.CartManager;
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.R;

public class PizzaDetailActivity extends AppCompatActivity {

    private PizzaDomain pizza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_detail);

        // Get pizza data from intent
        pizza = (PizzaDomain) getIntent().getSerializableExtra("pizza");

        if (pizza != null) {
            updateUIWithPizzaData();
        } else {
            // If no pizza data, go back to previous activity
            finish();
            return;
        }

        setupBottomNavigation();
        setupAddToCartButton();
        setupBackPressedHandler(); // Add this line
    }

    // Add this method to handle back press properly
    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void updateUIWithPizzaData() {
        // Initialize all views
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtDescription = findViewById(R.id.txtDescription);
        TextView txtRate = findViewById(R.id.txtRate);
        TextView txtTime = findViewById(R.id.txtTime);
        TextView txtTotalPrice = findViewById(R.id.txtTotalPrice);
        TextView txtItemPrice = findViewById(R.id.txtItemPrice);
        ImageView picPizza = findViewById(R.id.picPizza);

        // Set pizza data to views
        txtTitle.setText(pizza.getTitle());
        txtDescription.setText(pizza.getDescription());
        txtRate.setText(String.valueOf(pizza.getStar()));
        txtTime.setText(pizza.getTime() + "min");
        txtItemPrice.setText("Rs." + String.format("%.2f", pizza.getPrice()));
        txtTotalPrice.setText("Rs." + String.format("%.2f", pizza.getPrice()));

        // Load pizza image using Glide
        Glide.with(this)
                .load(pizza.getPicture())
                .placeholder(R.drawable.pizza1)
                .error(R.drawable.pizza1)
                .into(picPizza);

        // Setup quantity functionality
        setupQuantityFunctionality();
    }

    private void setupQuantityFunctionality() {
        TextView txtItemQuantity = findViewById(R.id.txtItemQuantity);
        TextView txtItemPrice = findViewById(R.id.txtItemPrice);
        TextView txtTotalPrice = findViewById(R.id.txtTotalPrice);
        View plusButton = findViewById(R.id.plusCardBtn);
        View minusButton = findViewById(R.id.minusCardBtn);

        if (plusButton != null && minusButton != null && txtItemQuantity != null &&
                txtItemPrice != null && txtTotalPrice != null) {

            final int[] quantity = {1};
            updateQuantityAndPrice(quantity[0], txtItemQuantity, txtItemPrice, txtTotalPrice);

            plusButton.setOnClickListener(v -> {
                quantity[0]++;
                updateQuantityAndPrice(quantity[0], txtItemQuantity, txtItemPrice, txtTotalPrice);
            });

            minusButton.setOnClickListener(v -> {
                if (quantity[0] > 1) {
                    quantity[0]--;
                    updateQuantityAndPrice(quantity[0], txtItemQuantity, txtItemPrice, txtTotalPrice);
                }
            });
        }
    }

    private void updateQuantityAndPrice(int quantity, TextView quantityView,
                                        TextView itemPriceView, TextView totalPriceView) {
        quantityView.setText(String.valueOf(quantity));

        String individualPrice = "Rs." + String.format("%.2f", pizza.getPrice());
        itemPriceView.setText(individualPrice);

        double totalPrice = pizza.getPrice() * quantity;
        totalPriceView.setText("Rs." + String.format("%.2f", totalPrice));
    }

    private void setupAddToCartButton() {
        View btnAddToCart = findViewById(R.id.btnAddToCart);
        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> addToCart());
        }
    }

//    private void addToCart() {
//        TextView txtItemQuantity = findViewById(R.id.txtItemQuantity);
//        int quantity = 1;
//
//        if (txtItemQuantity != null) {
//            try {
//                quantity = Integer.parseInt(txtItemQuantity.getText().toString());
//            } catch (NumberFormatException e) {
//                quantity = 1;
//            }
//        }
//
//        // TODO: Implement cart logic
//        android.widget.Toast.makeText(this, "Added to cart: " + pizza.getTitle(), android.widget.Toast.LENGTH_SHORT).show();
//    }


    private void addToCart() {
        TextView txtItemQuantity = findViewById(R.id.txtItemQuantity);
        int quantity = 1;

        if (txtItemQuantity != null) {
            try {
                quantity = Integer.parseInt(txtItemQuantity.getText().toString());
            } catch (NumberFormatException e) {
                quantity = 1;
            }
        }

        // Create a new PizzaDomain object and set its properties
        PizzaDomain pizzaToAdd = new PizzaDomain();
        pizzaToAdd.setTitle(pizza.getTitle());
        pizzaToAdd.setDescription(pizza.getDescription());
        pizzaToAdd.setPrice(pizza.getPrice());
        pizzaToAdd.setPicture(pizza.getPicture());
        pizzaToAdd.setStar(pizza.getStar());
        pizzaToAdd.setTime(pizza.getTime());
        pizzaToAdd.setQuantity(quantity);
        pizzaToAdd.setCategory(pizza.getCategory()); // If you have category field

        boolean added = CartManager.getInstance().addToCart(pizzaToAdd);

        if (added) {
            Toast.makeText(this, pizza.getTitle() + " added to cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, pizza.getTitle() + " is already in cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        findViewById(R.id.homeBtn).setOnClickListener(v -> {
            Intent intent = new Intent(PizzaDetailActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.profileBtn).setOnClickListener(v -> {
            Intent intent = new Intent(PizzaDetailActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cartBtn).setOnClickListener(v -> {
            Intent intent = new Intent(PizzaDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.branchesBtn).setOnClickListener(v -> {
            Intent intent = new Intent(PizzaDetailActivity.this, BranchesActivity.class);
            startActivity(intent);
        });
    }

    // REMOVE the onBackPressed() method completely
    // @Override
    // public void onBackPressed() {
    //     super.onBackPressed();
    //     finish();
    // }
}