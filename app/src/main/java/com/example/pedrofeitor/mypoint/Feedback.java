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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.pedrofeitor.mypoint.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class Feedback extends AppCompatActivity{

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    private FirebaseAuth mFirebaseAuth;
    public Button fullbuttom;
    String buspass;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        Bundle busnumber = getIntent().getExtras();
        buspass = busnumber.getString("busnumber");

        mFirebaseAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);
        fullbuttom = (Button) findViewById(R.id.full);
        Log.i("Feedback","onCreate");
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
