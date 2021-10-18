package com.example.mealshare.GUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.mealshare.Adapters.Delivery;
import com.example.mealshare.Adapters.Post;
import com.example.mealshare.FireBaseDB.CloudFireStore;
import com.example.mealshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class WantToDeliver extends AppCompatActivity {
    private ArrayList<Delivery> deliveries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_want_to_deliver);
        deliveries = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        deliveries.clear();
        getDeliveries();
    }

    private void getDeliveries() {
        CloudFireStore.getInstance().collection("deliveries")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult())
                            if(!document.contains("counter")) // if delivery document
                                deliveries.add(new Delivery(document));

                        deliveries.sort(null);
                        listOfDeliveries(deliveries);
                    } else {
                        finish();
                    }
                });
    }

    private void listOfDeliveries(ArrayList<Delivery> deliveries) {
        ((LinearLayout) findViewById(R.id.insideScroll)).removeAllViews();
        for (int i = 0; i < deliveries.size(); i++) {
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
            if (deliveries.get(i).getTitle().length() > 100)
                post.setText(deliveries.get(i).getTitle().substring(0, 100));
            else
                post.setText(deliveries.get(i).getTitle());
            post.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    com.example.mealshare.GUI.Delivery.delivery = deliveries.get(v.getId());
                    goToDelivery();
                }
            });
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            newline.addView(post, params);
            ((LinearLayout) findViewById(R.id.insideScroll)).addView(newline);
        }
    }

    private void goToDelivery() {
        startActivity(new Intent(this, com.example.mealshare.GUI.Delivery.class));
    }
}