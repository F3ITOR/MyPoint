package com.example.pedrofeitor.mypoint;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.pedrofeitor.mypoint.R.id.listview;

public class Test extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Declare Variables
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    String[] busnumberList;
    ArrayList<BusNumber> arraylist = new ArrayList<BusNumber>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    private FirebaseAuth mFirebaseAuth;

    protected EditText busText ;
    protected Button checkButtom;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Generate sample data

        busnumberList = new String[]{"112", "113", "114", "115"};
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

    public void confirm(View view){

        String bus = busText.getText().toString();

        if (bus.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Test.this);
            builder.setMessage(R.string.bus_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else{
            ref.child("users").child(mFirebaseAuth.getCurrentUser().getUid()).child("bus").setValue(bus);
            Intent intent = new Intent(Test.this, MapsActivity.class);
            startActivity(intent);
        }
    }
}
