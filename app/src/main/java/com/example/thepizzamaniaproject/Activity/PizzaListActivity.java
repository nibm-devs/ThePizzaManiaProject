package com.example.thepizzamaniaproject.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.thepizzamaniaproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PizzaListActivity extends AppCompatActivity {

    private TextView pizzaName, pizzaDescription, pizzaPrice;
    private ImageView pizzaImage;
    private Button btnAddToCart;

    private DatabaseReference cartRef;
    private String userId; // assuming FirebaseAuth is used

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_list);

        // Views
        pizzaName = findViewById(R.id.detailPizzaName);
        pizzaDescription = findViewById(R.id.detailPizzaDescription);
        pizzaPrice = findViewById(R.id.detailPizzaPrice);
        pizzaImage = findViewById(R.id.detailPizzaImage);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Firebase setup
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "guest";
        cartRef = FirebaseDatabase.getInstance("https://thepizzamaniaproject-default-rtdb.firebaseio.com/")
                .getReference("cart")
                .child(userId);

        // Get data from intent
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Set UI
        pizzaName.setText(name);
        pizzaDescription.setText(description);
        pizzaPrice.setText("$" + price);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_pizza)
                .into(pizzaImage);

        // Button: Add to Cart
        btnAddToCart.setOnClickListener(v -> {
            String itemId = cartRef.push().getKey();
            if (itemId != null) {
                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("name", name);
                cartItem.put("description", description);
                cartItem.put("price", price);
                cartItem.put("imageUrl", imageUrl);
                cartItem.put("quantity", 1);

                cartRef.child(itemId).setValue(cartItem)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(PizzaListActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(PizzaListActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        // Button: Order Now

    }
}
