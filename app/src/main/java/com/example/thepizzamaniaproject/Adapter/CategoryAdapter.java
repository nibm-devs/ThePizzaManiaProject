package com.example.thepizzamaniaproject.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;



import com.bumptech.glide.Glide;
import com.example.thepizzamaniaproject.Domain.CategoryDomain;
import com.example.thepizzamaniaproject.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>
{


        ArrayList<CategoryDomain> categoryDomains;
        private OnCategoryClickListener onCategoryClickListener;


        // Interface for click events
        public interface OnCategoryClickListener
        {
            void onCategoryClick(String categoryName);
        }

        public CategoryAdapter(ArrayList<CategoryDomain> categoryDomains)
        {
            this.categoryDomains = categoryDomains;
        }

        // Set the click listener
        public void setOnCategoryClickListener(OnCategoryClickListener listener)
        {
            this.onCategoryClickListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category,parent,false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {

//            holder.categoryName.setText(categoryDomains.get(position).getTitle());
//            String picUrl = "cat_1";


            // Get the category at this position
            CategoryDomain category = categoryDomains.get(position);

            // Set category name
            holder.categoryName.setText(category.getTitle());

            // Use the actual picture from the category object
            String picUrl = category.getPicture(); // This should be like "cat_1", "cat_2", etc.

//            int drawableResourceId = holder.itemView.getContext().getResources().getIdentifier(picUrl,"drawable",holder.itemView.getContext().getPackageName());
//            Glide.with(holder.itemView.getContext())
//                    .load(drawableResourceId)
//                    .into(holder.categoryPic);

            // Load the image using Glide
            int drawableResourceId = holder.itemView.getContext().getResources()
                    .getIdentifier(picUrl, "drawable", holder.itemView.getContext().getPackageName());

            Glide.with(holder.itemView.getContext())
                    .load(drawableResourceId)
                    .into(holder.categoryPic);




            // Set click listener on the main layout
            holder.mainLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onCategoryClickListener != null)
                    {
                        onCategoryClickListener.onCategoryClick(category.getTitle());
                    }
                }
            });


        }


        @Override
        public int getItemCount()
        {
            return categoryDomains.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder
        {

            TextView categoryName;
            ImageView categoryPic;

            ConstraintLayout mainLayout;


            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                categoryName = itemView.findViewById(R.id.categoryName);
                categoryPic = itemView.findViewById(R.id.categoryPic);
                mainLayout = itemView.findViewById(R.id.mainLayout);

            }
        }



}
