package com.example.pedrofeitor.mypoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ShowFeedback extends AppCompatActivity {
    String buspass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_feedback);

        Bundle busnumber = getIntent().getExtras();
        buspass = busnumber.getString("busnumber");
    }
}
