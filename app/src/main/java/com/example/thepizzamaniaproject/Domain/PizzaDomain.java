package com.example.thepizzamaniaproject.Domain;

public class PizzaDomain {

    private String title;
    private String picture;
    private String description;
    private Double price;
    private int star;
    private int time;



    // Constructor
    public PizzaDomain(String title, String picture, String description, Double price, int star, int time) {
        this.title = title;
        this.picture = picture;
        this.description = description;
        this.price = price;
        this.star = star;
        this.time = time;
    }


    // Getters
    public String getTitle() {
        return title;
    }

    public String getPicture() {
        return picture;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public int getStar() {
        return star;
    }

    public int getTime() {
        return time;
    }


    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
