package com.example.thepizzamaniaproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.thepizzamaniaproject.Activity.PizzaDetailActivity;
import com.example.thepizzamaniaproject.Helper.CartManager;
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.R;
import java.util.List;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.ViewHolder> {
    private List<PizzaDomain> pizzaList;
    private Context context;

    public PizzaAdapter(List<PizzaDomain> pizzaList) {
        this.pizzaList = pizzaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            PizzaDomain pizza = pizzaList.get(position);

            holder.txtTitle.setText(pizza.getTitle());
            holder.txtDescription.setText(pizza.getDescription());

            // Load image using Glide
            Glide.with(context)
                    .load(pizza.getPicture())
                    .placeholder(R.drawable.pizza1)
                    .error(R.drawable.pizza1)
                    .into(holder.pizzaImage);

            // Handle add to cart button click
            holder.addToCartBtn.setOnClickListener(v -> {
                // Create a new PizzaDomain object and set its properties
                PizzaDomain pizzaToAdd = new PizzaDomain();
                pizzaToAdd.setTitle(pizza.getTitle());
                pizzaToAdd.setDescription(pizza.getDescription());
                pizzaToAdd.setPrice(pizza.getPrice());
                pizzaToAdd.setPicture(pizza.getPicture());
                pizzaToAdd.setStar(pizza.getStar());
                pizzaToAdd.setTime(pizza.getTime());
                pizzaToAdd.setQuantity(1);  // Default quantity
                pizzaToAdd.setCategory(pizza.getCategory()); // If you have category field

                boolean added = CartManager.getInstance().addToCart(pizzaToAdd);

                if (added) {
                    Toast.makeText(context, pizza.getTitle() + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, pizza.getTitle() + " is already in cart", Toast.LENGTH_SHORT).show();
                }
            });

            holder.itemView.setOnClickListener(v -> {
                // Open pizza details activity
                Intent intent = new Intent(context, PizzaDetailActivity.class);
                intent.putExtra("pizza", pizza);
                context.startActivity(intent);
            });
        } catch (Exception e) {
            Log.e("PizzaAdapter", "Error binding view: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    public void updatePizzas(List<PizzaDomain> newPizzas) {
        this.pizzaList.clear();
        this.pizzaList.addAll(newPizzas);
        notifyDataSetChanged();
        Log.d("PizzaAdapter", "Updated with " + newPizzas.size() + " pizzas");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pizzaImage;
        TextView txtTitle, txtDescription;
        Button addToCartBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pizzaImage = itemView.findViewById(R.id.imageView5);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
        }
    }
}