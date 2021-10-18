package com.example.mealshare.GUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.mealshare.FireBaseDB.FireBaseStorage;
import com.example.mealshare.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Delivery extends AppCompatActivity {
    private TextView title, content, date, phone, address;
    public static com.example.mealshare.Adapters.Delivery delivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        title = findViewById(R.id.delivery_title);
        content = findViewById(R.id.deliveryContent);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.addressText);
        date = findViewById(R.id.date);

        title.setText(delivery.getTitle());
        content.setText(delivery.getContent());
        phone.setText(delivery.getPhone());
        address.setText(delivery.getAddress());
        date.setText(new SimpleDateFormat("dd/MM/yy HH:mm").format(new Date(delivery.getDate())));
    }

    public void gotoAddress(View view) {
        Intent i = new Intent(this, Map.class);
        i.putExtra("address", address.getText());
        startActivity(i);
    }

    public void gotoCurrentLocation(View view) {
        Intent i = new Intent(this, Map.class);
        i.putExtra("currentAddress", delivery.getCurrentAddress());
        startActivity(i);
    }

    public void removeDeliveryPost(View view) {
        delivery.getDelivery().getReference().delete().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Delivery Picked Successfully", Toast.LENGTH_SHORT);
                finish();
            }
        });
    }
}