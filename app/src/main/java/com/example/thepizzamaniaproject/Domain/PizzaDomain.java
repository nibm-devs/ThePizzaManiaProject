package com.example.thepizzamaniaproject.Domain;

import java.io.Serializable;

public class PizzaDomain implements Serializable {

    private String title;
    private String picture;
    private String description;
    private Double price;
    private Double star;
    private int time;
    private String category;


    public PizzaDomain()
    {

    }


    // Constructor
    public PizzaDomain(String title, String picture, String description, Double price, Double star, int time, String category) {
        this.title = title;
        this.picture = picture;
        this.description = description;
        this.price = price;
        this.star = star;
        this.time = time;
        this.category = category;
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

    public Double getStar() {
        return star;
    }

    public int getTime() {
        return time;
    }

    public String getCategory() {
        return category;
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

    public void setStar(Double star) {
        this.star = star;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
