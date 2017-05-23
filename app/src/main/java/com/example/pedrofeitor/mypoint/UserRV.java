package com.example.pedrofeitor.mypoint;

import java.util.*;

/**
 * Created by Pedro Feitor on 02/05/2017.
 */

public class UserRV {


    public String email;

    public List<UserRV> amigos = new ArrayList<UserRV>();
    public UserRV(){}

    public void setEmail(String email) {
        this.email = email;
    }

}