package com.example.ahsan.clintshareride.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ahsan.clintshareride.Model.Order;
import com.example.ahsan.clintshareride.R;

import java.util.List;

/**
 * Created by ahsan on 1/13/19.
 */

class MyViewHolder extends RecyclerView.ViewHolder{


    public TextView name, quantity,price,discount;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quality);
        price = itemView.findViewById(R.id.product_price);
        discount = itemView.findViewById(R.id.product_discount);
    }
}


public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Order order = myOrders.get(i);
        myViewHolder.name.setText(String.format("Person Name : %s",order.getPersonName()));
        myViewHolder.quantity.setText(String.format("Phone : %s",order.getPersonMobile()));
        myViewHolder.price.setText(String.format("Title : %s",order.getTitle()));
        myViewHolder.discount.setText(String.format("Details : %s",order.getDetails()));


    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}

