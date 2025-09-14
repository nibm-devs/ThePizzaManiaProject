package com.example.thepizzamaniaproject.Helper;

import com.example.thepizzamaniaproject.Domain.PizzaDomain;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<PizzaDomain> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public boolean addToCart(PizzaDomain pizza) {
        // Check if pizza already exists in cart by title
        for (PizzaDomain item : cartItems) {
            if (item.getTitle().equals(pizza.getTitle())) {
                // If it exists, just increase the quantity
                item.setQuantity(item.getQuantity() + pizza.getQuantity());
                return true;
            }
        }
        // If it doesn't exist, add it to the cart
        cartItems.add(pizza);
        return true;
    }

    public List<PizzaDomain> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void clearCart() {
        cartItems.clear();
    }
}