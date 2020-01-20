package com.example.ahsan.clintshareride;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class RequestDetailsActivity extends AppCompatActivity {

    private EditText mPostTitle;
    private EditText mPostDesc;

    private Button mSubmitBtn;

    private static final int GALLERY_REQUEST = 1;

    private Uri mImageUri = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ProgressBar mprogressBar;
    String driverFoundId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        if (getIntent() != null){
            driverFoundId = getIntent().getStringExtra("driverFoundID");
        }

        mAuth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");


        mPostTitle = (EditText) findViewById(R.id.title_Field);
        mPostDesc = (EditText) findViewById(R.id.desc_Field);
        mSubmitBtn = (Button) findViewById(R.id.submitButton);

        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);


        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        final String titleValue = mPostTitle.getText().toString().trim();
        final String descValue = mPostDesc.getText().toString().trim();

        if ((TextUtils.isEmpty(titleValue)) || (TextUtils.isEmpty(descValue))) {

            Toast.makeText(RequestDetailsActivity.this,"Field can't be empty",Toast.LENGTH_SHORT).show();

        }else {
            mprogressBar.setVisibility(View.VISIBLE);

            if (getIntent() != null){
                driverFoundId = getIntent().getStringExtra("driverFoundID");

                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId).child("customerRequest");
                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                HashMap map = new HashMap();
                map.put("customerRideId", customerId);
                map.put("title", titleValue);
                map.put("desc", descValue);
                driverRef.updateChildren(map);

                Toast.makeText(RequestDetailsActivity.this,"Posted successfully!!",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RequestDetailsActivity.this,CustomerMapActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(RequestDetailsActivity.this,"No Id for driver!!",Toast.LENGTH_SHORT).show();
            }




        }
    }
}
