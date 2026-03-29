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
            cart.setItems(new ArrayList<>()); // ✅ important
        }

        Product product = productRepo.findById(productId).orElseThrow();

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(qty);
        item.setCart(cart);

        cart.getItems().add(item);

        return cartRepo.save(cart);
    }

	public Cart getCartByUsername(String username) {
    User user = userRepo.findByUsername(username);
    return cartRepo.findByUser(user);
}
}
