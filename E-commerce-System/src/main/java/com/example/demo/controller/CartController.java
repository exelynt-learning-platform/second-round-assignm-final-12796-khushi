package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Cart;
import com.example.demo.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService service;

    // ✅ ADD TO CART
    @PostMapping("/add")
    public Cart addToCart(@RequestParam Long productId,
                          @RequestParam int quantity,
                          Principal principal) {

        if (principal == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not authenticated"
            );
        }

        return service.addToCart(principal.getName(), productId, quantity);
    }

    // ✅ GET USER CART
    @GetMapping
    public Cart getCart(Principal principal) {

        if (principal == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not authenticated"
            );
        }

        return service.getCartByUsername(principal.getName());
    }
}