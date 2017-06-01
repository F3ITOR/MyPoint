package com.example.pedrofeitor.mypoint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DatabaseReference busses;
    ArrayList<LatLng> paragens = new ArrayList<LatLng>();
    ArrayList<Marker> markerParagens=new ArrayList<>();
    private FirebaseAuth mFirebaseAuth;
    private MarkerOptions options;
    private Marker m;
    String buspass;
    public Button see;
    public Button feed;
    Distance d ;
    public ValueEventListener b;
    String state;
    double dist;
    LatLng latLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("passa","onCreate");
        setContentView(R.layout.activity_maps);
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
            d = new Distance();
            Bundle busnumber = getIntent().getExtras();
            buspass = busnumber.getString("busnumber");

            Bundle s =getIntent().getExtras();
            state = s.getString("state");
            see = (Button) findViewById(R.id.see);
            feed = (Button) findViewById(R.id.feed);
            Log.i("state", state);
            if (state.equals("future")){
                Log.i("ciclo","see");
                see.setVisibility(View.VISIBLE);
            }
            else{
                Log.i("ciclo","feed");
                feed.setVisibility(View.VISIBLE);
            }
            //Name.setText(userpass);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            Firebase.setAndroidContext(this);
            options = new MarkerOptions().position(new LatLng(38.751239,-9.60948)).title("BUS "+buspass);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("passa","onResume");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("passa","onStop");
        busses.removeEventListener(b);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);



        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        m=mMap.addMarker(options);
        DatabaseReference stops=ref.child("bus").child(buspass).child("paragens");
        stops.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(int i=1;i<4;i++){
                    String paragem=String.valueOf(i);
                    Coordenadas location=dataSnapshot.child(paragem).child("coordenadas").getValue(Coordenadas.class);
                    Boolean passou=(Boolean) dataSnapshot.child(paragem).child("passou").getValue();
                    Log.i("Paragem",String.valueOf(passou));
                    LatLng stop = new LatLng(location.latitude, location.longitude);
                    MarkerOptions stopOption;
                    if(passou){
                        stopOption = new MarkerOptions()
                                .position(stop)
                                .title("STOP"+String.valueOf(i))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                    else{
                        stopOption = new MarkerOptions()
                                .position(stop)
                                .title("STOP"+String.valueOf(i))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }
                    Marker mp=mMap.addMarker(stopOption);
                    paragens.add(stop);
                    markerParagens.add(mp);
                    Log.i("stop","passa");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.i("autocarro nº",buspass);
        busses=ref.child("bus").child(buspass).child("coordenadas");
        b=busses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Coordenadas location=dataSnapshot.getValue(Coordenadas.class);
                Log.i("autocarro latitude", String.valueOf(location.latitude));
                Log.i("autocarro longitude", String.valueOf(location.longitude));
                LatLng bus = new LatLng(location.latitude, location.longitude);
                m.setPosition(bus);
                Log.i("autocarro nparagens",String.valueOf(paragens.size()));
                for( int i=0;i<3;i++){
                    Log.i("distance","antes do get"+String.valueOf(i));
                    LatLng p = paragens.get(i);
                    Log.i("distance","depois do get");
                    dist = d.distance(p.latitude,p.longitude,bus.latitude,bus.longitude);
                    if (dist< .01) {
                        markerParagens.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        ref.child("bus").child(buspass).child("paragens").child(String.valueOf(i+1)).child("passou").setValue(true);
                        Log.i("paragemAutocarro", String.valueOf(i));
                    }else{
                        if (d.distance(latLng.latitude,latLng.longitude,p.latitude,p.longitude)<0.07 && state.equals("passanger")){
                            Log.i("check out", "done");
                        }
                    }
                }
                Log.i("nmap", "entrou");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(bus));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("onLocationChanged","passa");
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i("onLocationChanged lat",String.valueOf(location.getLatitude()));
        Log.i("onLocationChanged long",String.valueOf(location.getLongitude()));

        if(state.equals("passanger")) {
            Log.i("onLocationChanged", "passanger");
            ref.child("users/").child(mFirebaseAuth.getCurrentUser().getUid()).child("coordenadas").setValue(new Coordenadas(location.getLatitude(), location.getLongitude()));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public void onclickfeed(View view){
        Intent i = new Intent(this,Feedback.class);
        i.putExtra("busnumber", buspass);
        startActivity(i);
    }

    public  void onclicksee(View view){
        Intent i = new Intent(this,ShowFeedback.class);
        i.putExtra("busnumber", buspass);
        startActivity(i);
    }
}