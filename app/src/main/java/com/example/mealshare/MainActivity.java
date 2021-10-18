package com.example.mealshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mealshare.Adapters.Permissions;
import com.example.mealshare.GUI.NeedDelivery;
import com.example.mealshare.GUI.SearchMeal;
import com.example.mealshare.GUI.ShareMeal;
import com.example.mealshare.GUI.WantToDeliver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permissions.askForPermissions(this);
    }

    public void shareMeal(View view) {
        startActivity(new Intent(this, ShareMeal.class));
    }

    public void searchMeal(View view) {
        startActivity(new Intent(this, SearchMeal.class));
    }

    public void needDelivery(View view) {
        startActivity(new Intent(this, NeedDelivery.class));
    }

    public void wantToDeliver(View view) {
        startActivity(new Intent(this, WantToDeliver.class));
    }
}