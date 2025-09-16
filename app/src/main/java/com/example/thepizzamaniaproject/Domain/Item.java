package com.example.thepizzamaniaproject.Domain;



public class Item {
    private String name;
    private String description;
    private String imageUrl;
    private double price;

    public Item() {
        // Needed for Firebase
    }

    public Item(String name, String description, String imageUrl, double price) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getPrice() { return price; }
}

