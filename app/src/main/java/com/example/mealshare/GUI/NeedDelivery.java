package com.example.mealshare.GUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mealshare.FireBaseDB.CloudFireStore;
import com.example.mealshare.FireBaseDB.FireBaseStorage;
import com.example.mealshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NeedDelivery extends AppCompatActivity {
    private final int REQUEST = 112;
    private EditText title, content, address, phone;
    private int deliveryID;
    private double latitude, longitude;
    private String currentAddress;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_delivery);

        title = findViewById(R.id.post_title);
        content = findViewById(R.id.content);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);

        try {
            setCurrentLocationAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CloudFireStore.getInstance().collection("deliveries").document("counter").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) // get the post ID from DB
                            deliveryID = document.getLong("counter").intValue();
                        else deliveryID = 0;
                    }
                });
    }

    public void postDelivery(View view) {
        if (title.getText().toString().equals("")) {
            Toast.makeText(this, "Title is empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (content.getText().toString().equals("")) {
            Toast.makeText(this, "Content is empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone.getText().toString().equals("")) {
            Toast.makeText(this, "Phone is empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (address.getText().toString().equals("")) {
            Toast.makeText(this, "Address is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> post = new HashMap<>();
        post.put("title", title.getText().toString());
        post.put("content", content.getText().toString());
        post.put("deliveryID", deliveryID);
        post.put("date", (new Date()).getTime());
        post.put("address", address.getText().toString());
        post.put("phone", phone.getText().toString());
        post.put("currentAddress", currentAddress);

        CloudFireStore.getInstance().collection("deliveries")
                .document("" + deliveryID)
                .set(post)
                .addOnSuccessListener(aVoid -> {
                    updatePostsCounter();
                })
                .addOnFailureListener(e -> msg(false));
    }

    private void msg(boolean isSucceeded) {
        if (isSucceeded)
            Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Post upload failed", Toast.LENGTH_SHORT).show();
    }

    private void updatePostsCounter() {
        Map<String, Object> counter = new HashMap<>();
        counter.put("counter", ++deliveryID);

        CloudFireStore.getInstance().collection("deliveries").document("counter")
                .set(counter).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                msg(true);
                finish();
            } else msg(false);
        });
    }

    private void setCurrentLocationAddress() throws IOException {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST);
        } else {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                latitude = locationGPS.getLatitude();
                longitude = locationGPS.getLongitude();

                List<Address> addresses = new Geocoder(this, Locale.getDefault()).getFromLocation(latitude, longitude, 1);
                currentAddress = addresses.get(0).getAddressLine(0);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
