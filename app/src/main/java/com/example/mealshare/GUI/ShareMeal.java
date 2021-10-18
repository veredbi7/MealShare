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

public class ShareMeal extends AppCompatActivity {
    private final int CAMERA_REQUEST = 1888, REQUEST = 112;
    private ImageView image;
    private EditText title, content;
    private boolean photoTaken;
    private int postID;
    private double latitude, longitude;
    private String address;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_meal);

        image = findViewById(R.id.imageView);
        title = findViewById(R.id.post_title);
        content = findViewById(R.id.content);

        photoTaken = false;
        try {
            setCurrentLocationAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CloudFireStore.getInstance().collection("posts").document("counter").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) // get the post ID from DB
                                postID = document.getLong("counter").intValue();
                        }
                    }
                });
    }

    public void postMeal(View view) {
        if (title.getText().toString().equals("")) {
            Toast.makeText(this, "Title is empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (content.getText().toString().equals("")) {
            Toast.makeText(this, "Content is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> post = new HashMap<>();
        post.put("title", title.getText().toString());
        post.put("content", content.getText().toString());
        post.put("postID", postID);
        post.put("date", (new Date()).getTime());
        post.put("address", address);

        CloudFireStore.getInstance().collection("posts")
                .document("" + postID)
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (photoTaken)
                            uploadImage();
                        updatePostsCounter();
                        msg(true);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        msg(false);
                    }
                });
    }

    private void msg(boolean isSucceeded) {
        if (isSucceeded)
            Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Post upload failed", Toast.LENGTH_SHORT).show();
    }

    public void addPhoto(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        image.setImageBitmap(photo);
        photoTaken = true;
    }

    private void updatePostsCounter() {
        Map<String, Object> counter = new HashMap<>();
        counter.put("counter", ++postID);

        CloudFireStore.getInstance().collection("posts").document("counter")
                .set(counter).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void uploadImage() {
        // Get the data from an ImageView as bytes
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference mountainsRef = FireBaseStorage.getInstance().getReference().child(postID + ".jpg");

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
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
                address = addresses.get(0).getAddressLine(0);
            } else {
               // Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //call get location here
                } else {
                    Toast.makeText(this, "The app was not allowed to access your location", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
