package com.example.thepizzamaniaproject.Activity;

import android.annotation.SuppressLint;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thepizzamaniaproject.R;



import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thepizzamaniaproject.Adapter.DetailsAdapter;
import com.example.thepizzamaniaproject.Domain.Details;
import com.example.thepizzamaniaproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DetailsAdapter detailAdapter;
    private List<Details> orderList;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        detailAdapter = new DetailsAdapter(this, orderList);
        recyclerView.setAdapter(detailAdapter);

        databaseReference = FirebaseDatabase.getInstance("https://thepizzamaniaproject-default-rtdb.firebaseio.com/")
                .getReference("orders");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Details details = dataSnapshot.getValue(Details.class);
                    if (details != null) {
                        orderList.add(details);
                    }
                }
                detailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailsActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
