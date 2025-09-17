package com.example.thepizzamaniaproject.Adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thepizzamaniaproject.Domain.Details;
import com.example.thepizzamaniaproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.OrderViewHolder> {

    private Context context;
    private List<Details> orderList;

    public DetailsAdapter(Context context, List<Details> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Details order = orderList.get(position);

        holder.customerName.setText("Customer: " + order.getCustomerName());
        holder.pizzaName.setText("Pizza: " + order.getPizzaName());
        holder.quantity.setText("Qty: " + order.getQuantity());
        holder.price.setText("Price: $" + order.getPrice());
        holder.status.setText("Status: " + order.getStatus());

        DatabaseReference orderRef = FirebaseDatabase.getInstance("https://thepizzamaniaproject-default-rtdb.firebaseio.com/")
                .getReference("orders")
                .child(order.getOrderId());

        holder.btnAccept.setOnClickListener(v -> {
            orderRef.child("status").setValue("Accepted")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Order Accepted", Toast.LENGTH_SHORT).show();
                        holder.status.setText("Status: Accepted");
                    });
        });

        holder.btnDecline.setOnClickListener(v -> {
            orderRef.child("status").setValue("Declined")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Order Declined", Toast.LENGTH_SHORT).show();
                        holder.status.setText("Status: Declined");
                    });
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, pizzaName, quantity, price, status;
        Button btnAccept, btnDecline;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.orderCustomerName);
            pizzaName = itemView.findViewById(R.id.orderPizzaName);
            quantity = itemView.findViewById(R.id.orderQuantity);
            price = itemView.findViewById(R.id.orderPrice);
            status = itemView.findViewById(R.id.orderStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }
}

