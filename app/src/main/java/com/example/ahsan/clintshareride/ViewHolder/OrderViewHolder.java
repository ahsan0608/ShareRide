package com.example.ahsan.clintshareride.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.ahsan.clintshareride.R;

/**
 * Created by ahsan on 1/13/19.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView txtOrderId, txtOrderStatus, txtOrderphone, txtOrderAddress;

    public Button btnEdit, btnRemove, btnDetail, btnDirection;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderphone = itemView.findViewById(R.id.order_address);

        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnDetail = itemView.findViewById(R.id.btnDetail);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnDirection = itemView.findViewById(R.id.btnDirection);

    }



}