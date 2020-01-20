package com.example.ahsan.clintshareride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseActivity extends AppCompatActivity {

    Button bNeedHelp, bHelpingHand;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mAuth = FirebaseAuth.getInstance();
        bNeedHelp = findViewById(R.id.btnNeedHelp);
        bHelpingHand = findViewById(R.id.btnIwannaHelp);






        bNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_idC = mAuth.getCurrentUser().getUid();
//                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_idC);
//                current_user_db.setValue(true);
                Intent intent1 = new Intent(ChooseActivity.this, CustomerMapActivity.class);
                startActivity(intent1);
                finish();
            }
        });


        bHelpingHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_idD = mAuth.getCurrentUser().getUid();
//                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_idD);
//                current_user_db.setValue(true);
                Intent intent2 = new Intent(ChooseActivity.this, DriverMapActivity.class);
                startActivity(intent2);
                finish();
            }
        });
    }


}
