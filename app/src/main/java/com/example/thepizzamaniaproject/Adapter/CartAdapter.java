package com.example.thepizzamaniaproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.thepizzamaniaproject.Activity.CartActivity;
import com.example.thepizzamaniaproject.Helper.CartManager;
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.R;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<PizzaDomain> cartItems;
    private Context context;

    public CartAdapter(List<PizzaDomain> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PizzaDomain pizza = cartItems.get(position);

        holder.txtTitle.setText(pizza.getTitle());
        holder.txtEachPrice.setText("Rs." + String.format("%.2f", pizza.getPrice()));
        holder.txtItemQuantity.setText(String.valueOf(pizza.getQuantity()));

        double total = pizza.getPrice() * pizza.getQuantity();
        holder.txtEachTotal.setText("Rs." + String.format("%.2f", total));

        Glide.with(context)
                .load(pizza.getPicture())
                .placeholder(R.drawable.pizza1)
                .error(R.drawable.pizza1)
                .into(holder.imageView);

        // Add quantity adjustment functionality
        holder.plusCardBtn.setOnClickListener(v -> {
            int newQuantity = pizza.getQuantity() + 1;
            pizza.setQuantity(newQuantity);
            holder.txtItemQuantity.setText(String.valueOf(newQuantity));

            double newTotal = pizza.getPrice() * newQuantity;
            holder.txtEachTotal.setText("Rs." + String.format("%.2f", newTotal));

            updateCartTotals();
        });

        holder.minusCardBtn.setOnClickListener(v -> {
            if (pizza.getQuantity() > 1) {
                int newQuantity = pizza.getQuantity() - 1;
                pizza.setQuantity(newQuantity);
                holder.txtItemQuantity.setText(String.valueOf(newQuantity));

                double newTotal = pizza.getPrice() * newQuantity;
                holder.txtEachTotal.setText("Rs." + String.format("%.2f", newTotal));

                updateCartTotals();
            } else {
                // Remove item if quantity becomes 0
                // Use the position parameter instead of finding by object
                CartManager.getInstance().removeFromCart(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                updateCartTotals();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtEachPrice, txtItemQuantity, txtEachTotal;
        ImageView imageView, plusCardBtn, minusCardBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtEachPrice = itemView.findViewById(R.id.txtEachPrice);
            txtItemQuantity = itemView.findViewById(R.id.txtItemQuantity);
            txtEachTotal = itemView.findViewById(R.id.txtEachTotal);
            imageView = itemView.findViewById(R.id.imageView5);
            plusCardBtn = itemView.findViewById(R.id.plusCardBtn);
            minusCardBtn = itemView.findViewById(R.id.minusCardBtn);
        }
    }

    private void updateCartTotals() {
        if (context instanceof CartActivity) {
            ((CartActivity) context).updateCartTotals();
        }
    }
}