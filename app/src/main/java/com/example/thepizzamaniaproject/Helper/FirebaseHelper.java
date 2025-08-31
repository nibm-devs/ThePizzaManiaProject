package com.example.thepizzamaniaproject.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private StorageReference storage;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getDatabase() {
        return database;
    }

    public DatabaseReference getMenuReference() {
        return database.child("menuItems");
    }

    public DatabaseReference getBranchesReference() {
        return database.child("branches");
    }

    public DatabaseReference getUsersReference() {
        return database.child("users");
    }

    public DatabaseReference getOrdersReference() {
        return database.child("orders");
    }

    public DatabaseReference getRidersReference() {
        return database.child("riders");
    }

    public StorageReference getStorage() {
        return storage;
    }

    public StorageReference getMenuImagesReference() {
        return storage.child("menu_images");
    }
}
