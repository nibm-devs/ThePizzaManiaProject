package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.thepizzamaniaproject.R;

public class OrderSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        String orderId = getIntent().getStringExtra("ORDER_ID");
        TextView orderIdTextView = findViewById(R.id.orderIdTextView);
        orderIdTextView.setText("Order ID: " + orderId);

        // Set up continue shopping button
        findViewById(R.id.btnContinueShopping).setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}