package com.example.mealshare.FireBaseDB;

import com.google.firebase.firestore.FirebaseFirestore;

public class CloudFireStore {
    public static FirebaseFirestore getInstance()
    {
        return FirebaseFirestore.getInstance();
    }
}
