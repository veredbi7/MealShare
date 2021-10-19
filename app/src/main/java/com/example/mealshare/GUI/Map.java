package com.example.mealshare.GUI;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mealshare.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        address = getIntent().getExtras().getString("address");
        if (address == null)
            address = getIntent().getExtras().getString("currentAddress");

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private FusedLocationProviderClient fusedLocationClient;


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Context context = this.getApplicationContext();
        final LatLng[] position = new LatLng[1];
        if (address == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                position[0] = new LatLng(location.getLatitude(), location.getLongitude());
                                try {
                                    mMap.addMarker(new MarkerOptions().position(position[0]).title("Here! :)"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position[0]));

                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(position[0])
                                            .zoom(17).build();
                                    //Zoom in and animate the camera.
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                } catch (Exception e) {
                                    Toast.makeText(context, "Can't find this location", Toast.LENGTH_SHORT);
                                    finish();
                                }
                            }
                        }
                    });
        } else {
            position[0] = getLocationOfDelivery();
       /* Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                position[0] = getLocationOfDelivery();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
            // Add a marker and move the camera
            try {
                mMap.addMarker(new MarkerOptions().position(position[0]).title("Here! :)"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position[0]));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(position[0])
                        .zoom(17).build();
                //Zoom in and animate the camera.
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } catch (Exception e) {
                Toast.makeText(this, "Can't find this location", Toast.LENGTH_SHORT);
                finish();
            }
        }
    }


    private LatLng getLocationOfDelivery() {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        final LatLng[] p1 = {null};

        try {
            address = coder.getFromLocationName(this.address, 5);
            if (address == null)
                return null;

            if (address.size() == 0) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    p1[0] = new LatLng((double) (location.getLatitude()),
                                            (double) (location.getLongitude()));
                                }
                            }
                        });
            }
            else {
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();

                p1[0] = new LatLng((double) (location.getLatitude()),
                        (double) (location.getLongitude()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1[0];
    }
}