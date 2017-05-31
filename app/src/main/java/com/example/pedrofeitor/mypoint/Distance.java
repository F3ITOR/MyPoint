package com.example.pedrofeitor.mypoint;

import android.util.Log;

/**
 * Created by Pedro Feitor on 31/05/2017.
 */

public class Distance {

    public double distance(double lat1, double lng1, double lat2, double lng2) {
        Log.i("distance","inicio");
        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;
        Log.i("distance",String.valueOf(dist));
        return dist; // output distance, in MILES
    }
}
