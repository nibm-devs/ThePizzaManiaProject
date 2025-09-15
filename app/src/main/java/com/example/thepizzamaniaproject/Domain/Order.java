package com.example.thepizzamaniaproject.Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private String orderId;
    private String customerName;
    private String email;
    private String phone;
    private String gender;
    private String address;
    private double latitude;
    private double longitude;
    private double itemsTotal;
    private double tax;
    private double delivery;
    private double total;
    private long timestamp;
    private Map<String, PizzaDomain> items;

    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String customerName, String email, String phone, String gender,
                 String address, double latitude, double longitude,
                 double itemsTotal, double tax, double delivery, double total,
                 Map<String, PizzaDomain> items) {
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.itemsTotal = itemsTotal;
        this.tax = tax;
        this.delivery = delivery;
        this.total = total;
        this.timestamp = System.currentTimeMillis();
        this.items = items;
    }

    // Getters and setters for all fields
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getItemsTotal() { return itemsTotal; }
    public void setItemsTotal(double itemsTotal) { this.itemsTotal = itemsTotal; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getDelivery() { return delivery; }
    public void setDelivery(double delivery) { this.delivery = delivery; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Map<String, PizzaDomain> getItems() { return items; }
    public void setItems(Map<String, PizzaDomain> items) { this.items = items; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("customerName", customerName);
        result.put("email", email);
        result.put("phone", phone);
        result.put("gender", gender);
        result.put("address", address);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("itemsTotal", itemsTotal);
        result.put("tax", tax);
        result.put("delivery", delivery);
        result.put("total", total);
        result.put("timestamp", timestamp);
        result.put("items", items);
        return result;
    }
}
