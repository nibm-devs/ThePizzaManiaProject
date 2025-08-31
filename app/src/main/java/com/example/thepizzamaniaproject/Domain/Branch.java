package com.example.thepizzamaniaproject.Domain;
public class Branch {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String manager;
    private boolean isActive;
    private double latitude;
    private double longitude;

    public Branch() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
