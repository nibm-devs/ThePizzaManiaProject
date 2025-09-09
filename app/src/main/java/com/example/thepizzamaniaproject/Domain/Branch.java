package com.example.thepizzamaniaproject.Domain;

public class Branch {
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String phone;
    private String manager;
    private long timestamp;

    public Branch() {
        // Default constructor required for Firebase
    }

    public Branch(String name, String address, double latitude, double longitude, String phone, String manager) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.manager = manager;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}