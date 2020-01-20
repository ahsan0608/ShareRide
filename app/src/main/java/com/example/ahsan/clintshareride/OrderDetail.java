package com.example.ahsan.clintshareride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ahsan.clintshareride.ViewHolder.OrderDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderDetail extends AppCompatActivity {

    TextView order_id, order_phone, order_address, order_total, order_comment;
    String order_id_value ="";
    RecyclerView lstFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);



        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.order_phone);
        order_address = findViewById(R.id.order_address);
        order_total = findViewById(R.id.order_total);
        order_comment = findViewById(R.id.order_comment);

        lstFoods = findViewById(R.id.lstFoods);
        lstFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);


//        if (getIntent() != null){
//            order_id_value = getIntent().getStringExtra("OrderId");
//        }



//        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID);
//        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
//                    if(dataSnapshot.child("name")!=null){
//                        mDriverName.setText(dataSnapshot.child("name").getValue().toString());
//                    }
//                    if(dataSnapshot.child("phone")!=null){
//                        mDriverPhone.setText(dataSnapshot.child("phone").getValue().toString());
//                    }
//                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
//                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mDriverProfileImage);
//                    }
//
//                    int ratingSum = 0;
//                    float ratingsTotal = 0;
//                    float ratingsAvg = 0;
//                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
//                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
//                        ratingsTotal++;
//                    }
//                    if(ratingsTotal!= 0){
//                        ratingsAvg = ratingSum/ratingsTotal;
//                        mRatingBar.setRating(ratingsAvg);
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });





        order_id.setText(order_id_value);
        order_phone.setText("Customer Name");
        order_address.setText("Customer Phone");
        order_total.setText("Title");
        order_comment.setText("Details");


//        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
//        adapter.notifyDataSetChanged();
//        lstFoods.setAdapter(adapter);

   // }
    }
}
