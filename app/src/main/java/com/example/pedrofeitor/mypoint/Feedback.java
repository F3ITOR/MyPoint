package com.example.pedrofeitor.mypoint;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.pedrofeitor.mypoint.Test.MY_PERMISSIONS_REQUEST_LOCATION;

public class Feedback extends AppCompatActivity{

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    private FirebaseAuth mFirebaseAuth;
    public Button fullbuttom;
    String buspass;
    String state;
    Intent intent;
    protected Button full;
    protected Button clean;
    protected TextView fullText;
    protected TextView cleanText;
    public long fullValue;
    public long cleanValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        intent = new Intent(Feedback.this, MapsActivity.class);
        Bundle busnumber = getIntent().getExtras();
        buspass = busnumber.getString("busnumber");
        state=busnumber.getString("state");
        mFirebaseAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);
        //fullbuttom = (Button) findViewById(R.id.full);
        Log.i("Feedback",state);

        if(state.equals("future")){
            DatabaseReference feedback=ref.child("bus").child(buspass).child("feedback");
            feedback.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cleanValue=(long) dataSnapshot.child("clean").child("np").getValue();
                    fullValue=(long) dataSnapshot.child("full").child("np").getValue();
                    Log.i("value clean",String.valueOf(cleanValue));
                    Log.i("value full",String.valueOf(fullValue));
                    fullText = (TextView) findViewById(R.id.fullText);

                    cleanText = (TextView) findViewById(R.id.cleanText);

                    cleanText.append(" - "+String.valueOf(cleanValue));
                    fullText.append(" - "+String.valueOf(fullValue));
                    fullText.setVisibility(View.VISIBLE);
                    cleanText.setVisibility(View.VISIBLE);

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            full=(Button) findViewById(R.id.full);
            full.setVisibility(View.VISIBLE);
            clean=(Button) findViewById(R.id.clean);
            clean.setVisibility(View.VISIBLE);
        }
    }

    public void onclickfull(View view) {
        ref.child("bus").child(buspass).child("passageiros").child(mFirebaseAuth.getCurrentUser().getUid().toString()).child("feedback").child("full").setValue(1);
        finish();
    }

    public void onclickclean(View view) {
        ref.child("bus").child(buspass).child("passageiros").child(mFirebaseAuth.getCurrentUser().getUid().toString()).child("feedback").child("clean").setValue(1);
        finish();
    }

}
