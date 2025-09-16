package com.example.thepizzamaniaproject.Adapter;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thepizzamaniaproject.Activity.PizzaDetailActivity;
import com.example.thepizzamaniaproject.Domain.Item;
import com.example.thepizzamaniaproject.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.PizzaViewHolder> {
    private Context context;
    private List<Item> pizzaList;

    public ItemAdapter(Context context, List<Item> pizzaList) {
        this.context = context;
        this.pizzaList = pizzaList;
    }

    @NonNull
    @Override
    public PizzaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pizza, parent, false);
        return new PizzaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PizzaViewHolder holder, int position) {
        Item Item = pizzaList.get(position);
        holder.name.setText(Item.getName());
        holder.description.setText(Item.getDescription());
        holder.price.setText("$" + Item.getPrice());

        Glide.with(context)
                .load(Item.getImageUrl())
                .placeholder(R.drawable.placeholder_pizza)
                .into(holder.image);

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PizzaDetailActivity.class);
            intent.putExtra("name", Item.getName());
            intent.putExtra("description", Item.getDescription());
            intent.putExtra("price", Item.getPrice());
            intent.putExtra("imageUrl", Item.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    public static class PizzaViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, price;
        ImageView image;

        public PizzaViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pizzaName);
            description = itemView.findViewById(R.id.pizzaDescription);
            price = itemView.findViewById(R.id.pizzaPrice);
            image = itemView.findViewById(R.id.pizzaImage);
        }
    }
}

