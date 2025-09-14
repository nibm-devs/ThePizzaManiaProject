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
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.R;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {
    private List<PizzaDomain> pizzaList;
    private Context context;

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(PizzaDomain pizza);
    }

    private OnItemClickListener listener;

    public RecommendedAdapter(List<PizzaDomain> pizzaList) {
        this.pizzaList = pizzaList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_recommended, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PizzaDomain pizza = pizzaList.get(position);

        holder.title.setText(pizza.getTitle());
        holder.price.setText("Rs." + String.format("%.2f", pizza.getPrice()));

        // Load image using Glide
        Glide.with(context)
                .load(pizza.getPicture())
                .placeholder(R.drawable.pizza1)
                .error(R.drawable.pizza1)
                .into(holder.pic);

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(pizza);
            }
        });

        // Set click listener for the add button
        holder.addBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(pizza);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    public void updatePizzas(List<PizzaDomain> newPizzas) {
        this.pizzaList.clear();
        this.pizzaList.addAll(newPizzas);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic, addBtn;
        TextView title, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            addBtn = itemView.findViewById(R.id.addBtn);
        }
    }
}