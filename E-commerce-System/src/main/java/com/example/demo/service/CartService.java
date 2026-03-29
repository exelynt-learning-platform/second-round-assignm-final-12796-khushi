package com.example.demo.service;


import com.example.demo.entity.*;
import com.example.demo.repository.*;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired private CartRepository cartRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private UserRepository userRepo;

    public Cart addToCart(String username, Long productId, int qty) {

        User user = userRepo.findByUsername(username);
        Cart cart = cartRepo.findByUser(user);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>()); // ✅ IMPORTANT
        }

        Product product = productRepo.findById(productId).orElseThrow();

        // 🔥 1. STOCK VALIDATION
        if (product.getStock() < qty) {
            throw new RuntimeException("Not enough stock");
        }

        // 🔥 2. CHECK IF PRODUCT ALREADY EXISTS
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + qty);
                return cartRepo.save(cart);
            }
        }

        // 🔥 3. CREATE NEW ITEM ONLY IF NOT EXISTS
        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(qty);
        newItem.setCart(cart);

        cart.getItems().add(newItem);

        return cartRepo.save(cart);
    }

    public Cart getCartByUsername(String username) {

        User user = userRepo.findByUsername(username);
        Cart cart = cartRepo.findByUser(user);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            return cartRepo.save(cart);
        }

        return cart;
    }

	
}
