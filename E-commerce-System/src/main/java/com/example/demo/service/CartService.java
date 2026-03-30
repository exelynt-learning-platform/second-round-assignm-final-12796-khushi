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

    @Autowired
    private UserRepository userRepo;

    // ✅ ADD TO CART
    public Cart addToCart(String username, Long productId, int qty) {

        // ✅ USER VALIDATION
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ GET CART
        Cart cart = cartRepo.findByUser(user);

        // ✅ CREATE CART IF NOT EXISTS
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            cart = cartRepo.save(cart); // ✅ IMPORTANT
        }

        // ✅ EXTRA SAFETY
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        // ✅ PRODUCT VALIDATION
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ QUANTITY VALIDATION
        if (qty <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        // ✅ STOCK VALIDATION
        if (product.getStock() < qty) {
            throw new RuntimeException("Not enough stock");
        }

        // ✅ CHECK IF PRODUCT EXISTS IN CART
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + qty);
                return cartRepo.save(cart);
            }
        }

        // ✅ ADD NEW ITEM
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

        Cart cart = cartRepo.findByUser(user);

        // ✅ CREATE IF NOT EXISTS
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            return cartRepo.save(cart);
        }

        // ✅ EXTRA SAFETY
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        return cart;
    }
}