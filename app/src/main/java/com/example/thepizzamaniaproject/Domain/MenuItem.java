package com.example.thepizzamaniaproject.Domain;



public class MenuItem {
    private String id;
    private String title;
    private String description;
    private double price;
    private String picture;
    private String category;
    private double star;
    private int time;
    private boolean available;

    public MenuItem() {}

    public MenuItem(String id, String title, String description, double price,
                    String picture, String category, double star, int time, boolean available) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.picture = picture;
        this.category = category;
        this.star = star;
        this.time = time;
        this.available = available;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getStar() { return star; }
    public void setStar(double star) { this.star = star; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}