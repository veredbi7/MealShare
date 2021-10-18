package com.example.mealshare.GUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealshare.Adapters.Post;
import com.example.mealshare.FireBaseDB.CloudFireStore;
import com.example.mealshare.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SearchMeal extends AppCompatActivity {
    private final int REQUEST = 112;
    private ArrayList<Post> posts;
    private TextView radius;
    private double myLatitude, myLongitude;
    private Button applyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_meal);
        posts = new ArrayList<>();
        radius = findViewById(R.id.radius);
        applyBtn = findViewById(R.id.apply);

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            posts.clear();
            setCurrentLocationAddress();
            getPosts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getPosts() {
        CloudFireStore.getInstance().collection("posts")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult())
                            if (!document.contains("counter")) // if post document
                            {
                                try {
                                    Post post = new Post(document);
                                    LatLng mealPosition = getLocationOfMeal(SearchMeal.this, post.getAddress());
                                    if (radius.getText().toString().equals("") ||
                                            !radius.getText().equals("") &&
                                                    distance(mealPosition.latitude, mealPosition.longitude, myLatitude, myLongitude) <= Integer.parseInt(radius.getText().toString()))
                                        posts.add(post);
                                } catch (Exception e) {
                                }
                            }
                        posts.sort(null);
                        listOfPosts(posts);
                    } else {
                        finish();
                    }
                });
    }

    private void listOfPosts(ArrayList<Post> posts) {
        ((LinearLayout) findViewById(R.id.insideScroll)).removeAllViews();
        ((LinearLayout) findViewById(R.id.insideScroll)).invalidate();
        ((LinearLayout) findViewById(R.id.insideScroll)).requestLayout();

        for (int i = 0; i < posts.size(); i++) {
            //Params var for all views.
            RelativeLayout.LayoutParams params;
            //New line of a pending supporter with settings.
            RelativeLayout newline = new RelativeLayout(this);
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            newline.setLayoutParams(params);
            //Button as post
            Button post = new Button(this);
            post.setId(i);
            post.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            if (posts.get(i).getTitle().length() > 100)
                post.setText(posts.get(i).getTitle().substring(0, 100));
            else
                post.setText(posts.get(i).getTitle());
            post.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    com.example.mealshare.GUI.Post.post = posts.get(v.getId());
                    goToPost();
                }
            });
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            newline.addView(post, params);
            ((LinearLayout) findViewById(R.id.insideScroll)).addView(newline);
        }
    }

    private void goToPost() {
        startActivity(new Intent(this, com.example.mealshare.GUI.Post.class));
    }

    private void setCurrentLocationAddress() throws IOException {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST);
        } else {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                myLatitude = locationGPS.getLatitude();
                myLongitude = locationGPS.getLongitude();
            } else {
                // Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public LatLng getLocationOfMeal(Context context, String address) {
        Geocoder coder = new Geocoder(context);
        List<Address> addresses;
        LatLng p1 = null;

        try {
            addresses = coder.getFromLocationName(address, 5);
            if (address == null) {
                return null;
            }
            Address location = addresses.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    public void applyOnClick(View view) {
        posts.removeAll(posts);
        try {
            setCurrentLocationAddress();
            getPosts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}