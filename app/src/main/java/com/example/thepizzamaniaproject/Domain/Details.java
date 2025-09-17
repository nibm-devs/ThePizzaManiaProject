package com.example.thepizzamaniaproject.Domain;

public class Details {
    private String orderId;
    private String customerName;
    private String pizzaName;
    private int quantity;
    private double price;
    private String status;

    public Details() { } // required for Firebase

    public Details(String orderId, String customerName, String pizzaName, int quantity, double price, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.pizzaName = pizzaName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getPizzaName() { return pizzaName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }

    // Setters (optional but useful)
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setPizzaName(String pizzaName) { this.pizzaName = pizzaName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
}
