package com.example.pedrofeitor.mypoint;

import java.util.*;

/**
 * Created by Pedro Feitor on 02/05/2017.
 */

public class UserRV {
    private String nome;
    private double latitude;
    private double longitude;
    private List<UserRV> amigos = new ArrayList<UserRV>();

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}