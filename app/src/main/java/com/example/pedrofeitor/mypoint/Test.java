package com.example.pedrofeitor.mypoint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.example.pedrofeitor.mypoint.R.id.listview;

public class Test extends AppCompatActivity implements SearchView.OnQueryTextListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    // Declare Variables
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    String[] busnumberList;
    ArrayList<BusNumber> arraylist = new ArrayList<BusNumber>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    private FirebaseAuth mFirebaseAuth;
    Distance d;
    protected EditText busText ;
    protected Button checkButtom;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    LatLng UserCoord;
    double distance;
    String bus;
    Boolean check=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Generate sample data
        d = new Distance();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        busnumberList = new String[]{"112", "113"};
        busText = (EditText) findViewById(R.id.busnumber);
        checkButtom = (Button) findViewById(R.id.loginButton);

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(listview);

        for (int i = 0; i < busnumberList.length; i++) {
            BusNumber animalNames = new BusNumber(busnumberList[i]);
            // Binds all strings into an array
            arraylist.add(animalNames);
        }

        // Pass results to ListViewAdapter Class
        adapter = new ListViewAdapter(this, arraylist);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }

    public void Click(View view){
        Button buttom = (Button) findViewById(R.id.checkin);
        buttom.setVisibility(View.VISIBLE);
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
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            Log.i("latitude","onStart");
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i("latitude","onLoctionChanged");
        UserCoord = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i("coordendas latitude",String.valueOf(location.getLatitude()));
        Log.i("coordendas longitude",String.valueOf(location.getLongitude()));
    }

    public void search(View view){
        Intent i = new Intent(this,MapsActivity.class);

        final SearchView busnumber = (SearchView) findViewById(R.id.search);

        String busnumberinput = busnumber.getQuery().toString();

        if (busnumberinput.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Test.this);
            builder.setMessage(R.string.bus_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else{
            i.putExtra("busnumber", busnumberinput);
            i.putExtra("state", "future");
            startActivity(i);
        }
    }

    public void confirm(View view){
        Intent intent = new Intent(Test.this, MapsActivity.class);
        String bus = busText.getText().toString();

        DatabaseReference busses=ref.child("bus").child(bus).child("coordenadas");
        ValueEventListener bv= busses.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Coordenadas location=dataSnapshot.getValue(Coordenadas.class);
                Log.i("autocarro latitude", String.valueOf(location.latitude));
                Log.i("autocarro longitude", String.valueOf(location.longitude));

                Log.i("autocarro userlatitude", String.valueOf(UserCoord.latitude));
                Log.i("autocarro userlongitude", String.valueOf(UserCoord.longitude));

                distance = d.distance(location.latitude,location.longitude,UserCoord.latitude,UserCoord.longitude);
                Log.i("autocarro distance", String.valueOf(distance));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (bus.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Test.this);
            builder.setMessage(R.string.bus_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else{
            busses.removeEventListener(bv);
            intent.putExtra("busnumber", bus);
            intent.putExtra("state","passanger");
            ref.child("users/").child(mFirebaseAuth.getCurrentUser().getUid()).child("bus").setValue(Integer.parseInt(bus));
            startActivity(intent);
        }
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
}
