package com.example.mealshare.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class Permissions {
    private static final int REQUEST_CODE = 1;

    public static void askForPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForCameraPerm(activity);
            askForFilesPerm(activity);
        }
    }

    public static void askForCameraPerm(Activity activity) {
        if (activity.getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            // if permission for camera isn't permitted than ask the user to grant it
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
    }

    public static void askForFilesPerm(Activity activity) {
        if (activity.getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            // if permission for gallery and files isn't permitted than ask the user to grant it
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }
}

