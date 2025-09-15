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
import com.example.thepizzamaniaproject.R;
import com.example.thepizzamaniaproject.Domain.MenuItem;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<MenuItem> menuItemList;
    private Context context;
    private boolean isAdmin;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, MenuItem menuItem);
        void onEditClick(int position, MenuItem menuItem);
        void onDeleteClick(int position, MenuItem menuItem);
        void onToggleStatusClick(int position, MenuItem menuItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public MenuAdapter(List<MenuItem> menuItemList, Context context, boolean isAdmin) {
        this.menuItemList = menuItemList;
        this.context = context;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItemList.get(position);

        holder.tvTitle.setText(menuItem.getTitle());
        holder.tvDescription.setText(menuItem.getDescription());
        holder.tvPrice.setText(String.format("$%.2f", menuItem.getPrice()));
        holder.tvCategory.setText(menuItem.getCategory());
        holder.tvRating.setText(String.valueOf(menuItem.getStar()));
        holder.tvTime.setText(menuItem.getTime() + " min");

        // Load image with Glide
        if (menuItem.getPicture() != null && !menuItem.getPicture().isEmpty()) {
            Glide.with(context)
                    .load(menuItem.getPicture())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.placeholder_image);
        }

        // Show admin controls if in admin mode
        if (isAdmin) {
            holder.tvAvailability.setVisibility(View.VISIBLE);
            holder.tvAvailability.setText(menuItem.isAvailable() ? "Available" : "Not Available");
            holder.tvAvailability.setTextColor(context.getResources().getColor(
                    menuItem.isAvailable() ? R.color.green : R.color.primary_red));

            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.VISIBLE);

            // Set click listeners for admin actions
            holder.ivEdit.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onEditClick(position, menuItem);
                }
            });

            holder.ivDelete.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onDeleteClick(position, menuItem);
                }
            });

            holder.tvAvailability.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onToggleStatusClick(position, menuItem);
                }
            });
        } else {
            holder.tvAvailability.setVisibility(View.GONE);
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
        }

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, menuItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItemList != null ? menuItemList.size() : 0;
    }

    public void updateData(List<MenuItem> newMenuItems) {
        menuItemList = newMenuItems;
        notifyDataSetChanged();
    }

    public void updateItem(int position, MenuItem menuItem) {
        if (menuItemList != null && position < menuItemList.size()) {
            menuItemList.set(position, menuItem);
            notifyItemChanged(position);
        }
    }

    public void removeItem(int position) {
        if (menuItemList != null && position < menuItemList.size()) {
            menuItemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addItem(MenuItem menuItem) {
        if (menuItemList != null) {
            menuItemList.add(menuItem);
            notifyItemInserted(menuItemList.size() - 1);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPrice, tvCategory, tvRating, tvTime, tvAvailability;
        ImageView ivImage, ivEdit, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}