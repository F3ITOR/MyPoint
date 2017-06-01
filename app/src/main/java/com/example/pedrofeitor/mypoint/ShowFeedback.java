package com.example.pedrofeitor.mypoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowFeedback extends AppCompatActivity {
    String buspass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Feedback Show","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_feedback);

        Bundle busnumber = getIntent().getExtras();
        buspass = busnumber.getString("busnumber");

        DatabaseReference stops=FirebaseDatabase.getInstance().getReference().child("bus").child(buspass).child("feedback");
        stops.addListenerForSingleValueEvent(new ValueEventListener() {

            String text;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("full").exists()){
                    Boolean passou=(Boolean) dataSnapshot.child("full").child("state").getValue();
                    Long np=(Long) dataSnapshot.child("full").child("np").getValue();
                    text=String.valueOf(np)+" pessoas disseram que este autocarro estava cheio";
                    Log.i("Feedback",text);
                }
                if(dataSnapshot.child("clean").exists()){
                    Log.i("Feedback clean","npassageiros");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



}
