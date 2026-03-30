package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private ProductRepository productRepo;

    // ✅ COMMON METHOD (NEW)
    private Cart getOrCreateCart(User user) {

        Cart cart = cartRepo.findByUser(user);

        // ✅ CREATE CART IF NOT EXISTS
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            return cartRepo.save(cart);
        }

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        return cart;
    }

    // ✅ ADD TO CART
    public Cart addToCart(String username, Long productId, int qty) {

        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ USE COMMON METHOD
        Cart cart = getOrCreateCart(user);

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (qty <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        if (product.getStock() < qty) {
            throw new RuntimeException("Not enough stock");
        }

        // ✅ check if already exists
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + qty);
                return cartRepo.save(cart);
            }
        }

        // ✅ add new item
        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(qty);
        newItem.setCart(cart);

        cart.getItems().add(newItem);

        return cartRepo.save(cart);
    }

    // ✅ GET CART
    public Cart getCartByUsername(String username) {

        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ REUSE METHOD
        return getOrCreateCart(user);
    }
}