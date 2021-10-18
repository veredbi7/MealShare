package com.example.mealshare.Adapters;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Post implements Comparable {
    private String title, content, address;
    private long date;
    private DocumentSnapshot post;
    private int id;

    public Post(DocumentSnapshot doc) {
        try {
            title = (String) doc.get("title");
            content = (String) doc.get("content");
            address = (String) doc.get("address");
            date = (long) doc.get("date");
            post = doc;
            id = doc.getDouble("postID").intValue();
        } catch (Exception e) {
        }
    }

    public String getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getDate() {
        return date;
    }

    public DocumentSnapshot getPost() {
        return post;
    }

    public int getPostID() {
        return id;
    }

    @Override
    public int compareTo(Object p) {
        return (int) (((Post) p).date - date);
    }
}
