package com.example.thepizzamaniaproject.viewmodel;

import androidx.lifecycle.ViewModel;

public class MenuViewModel extends ViewModel {
    public String imageUri;
    public String name;
    public String description;
    public String price;
    public String category;

    public void openImageChooser() {
        // This will be handled by the activity
    }

    public void saveMenuItem() {
        // This will be handled by the activity
    }
}