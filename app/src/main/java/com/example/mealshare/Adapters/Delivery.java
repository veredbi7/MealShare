package com.example.mealshare.Adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class Delivery implements Comparable {
    private String title;
    private String content;
    private String phone;
    private String address;
    private String currentAddress;
    private long date;
    private DocumentSnapshot delivery;
    private int id;

    public Delivery(DocumentSnapshot doc) {
        try {
            title = (String) doc.get("title");
            content = (String) doc.get("content");
            date = (long) doc.get("date");
            phone = (String) doc.get("phone");
            address = (String) doc.get("address");
            currentAddress = (String) doc.get("currentAddress");
            delivery = doc;
            id = doc.getDouble("deliveryID").intValue();
        } catch (Exception e) {
        }
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public DocumentSnapshot getDelivery() {
        return delivery;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    public long getDate() {
        return date;
    }

    @Override
    public int compareTo(Object p) {
        return (int) (((Delivery) p).getDate() - date);
    }
}
