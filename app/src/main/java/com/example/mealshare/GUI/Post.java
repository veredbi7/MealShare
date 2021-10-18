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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Post extends AppCompatActivity {
    private TextView title, content, date;
    private ImageView postImage;
    public static com.example.mealshare.Adapters.Post post;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title = findViewById(R.id.post_title);
        content = findViewById(R.id.postContent);
        postImage = findViewById(R.id.postImg);
        date = findViewById(R.id.date);

        title.setText(post.getTitle());
        content.setText(post.getContent());

        date.setText(new SimpleDateFormat("dd/MM/yy HH:mm").format(new Date(post.getDate())));
        context = this;
        setPostImage();
    }

    private void setPostImage() {
        FireBaseStorage.getInstance().getReference().child(post.getPostID() + ".jpg")
                .getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                postImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, postImage.getWidth(), postImage.getHeight(), false));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                postImage.getLayoutParams().width = 0;
                postImage.getLayoutParams().height = 0;

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ((TextView) findViewById(R.id.postContent)).getLayoutParams();
                params.width = 888;
                ((TextView) findViewById(R.id.postContent)).setLayoutParams(params);
            }
        });

        postImage.setOnClickListener(v -> {
            final ImagePopup imagePopup = new ImagePopup(context);
            imagePopup.initiatePopup(postImage.getDrawable()); // Load Image from Drawable
            imagePopup.viewPopup(); // view popup of the image
        });

    }

    public void goto_Map(View view) {
        Intent i = new Intent(this, Map.class);
        i.putExtra("address", post.getAddress());
        startActivity(i);
    }

    public void removeMealPost(View view) {
        post.getPost().getReference().delete().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Meal Picked Successfully", Toast.LENGTH_SHORT);

                FireBaseStorage.getInstance().getReference().child(post.getPostID() + ".jpg").delete()
                        .addOnCompleteListener(task1 -> {
                            Toast.makeText(this, "Meal Image Deleted Successfully", Toast.LENGTH_SHORT);
                            finish();
                        });
            }
        });
    }
}