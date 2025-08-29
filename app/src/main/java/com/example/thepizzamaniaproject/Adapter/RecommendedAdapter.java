package com.example.thepizzamaniaproject.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thepizzamaniaproject.Domain.PizzaDomain;
import com.example.thepizzamaniaproject.R;

import java.util.ArrayList;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder>
{


        ArrayList<PizzaDomain> RecommendedDomains;


        public RecommendedAdapter(ArrayList<PizzaDomain> pizzaDomains)
        {
            this.RecommendedDomains = pizzaDomains;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_recommended,parent,false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {

            holder.title.setText(RecommendedDomains.get(position).getTitle());
            holder.price.setText(String.valueOf(RecommendedDomains.get(position).getPrice()));


            int drawableResourceId = holder.itemView.getContext().getResources().getIdentifier(RecommendedDomains.get(position).getPicture(),"drawable",holder.itemView.getContext().getPackageName());
            Glide.with(holder.itemView.getContext())
                    .load(drawableResourceId)
                    .into(holder.pic);


        }


        @Override
        public int getItemCount()
        {
            return RecommendedDomains.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder
        {

            TextView title,price;
            ImageView pic;
            ImageView addBtn;



            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                title = itemView.findViewById(R.id.title);
                pic = itemView.findViewById(R.id.pic);
                price = itemView.findViewById(R.id.price);
                addBtn = itemView.findViewById(R.id.addBtn);
   

            }
        }



}
