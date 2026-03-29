package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Cart;
import com.example.demo.service.CartService;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService service;

    @PostMapping("/add")
    public Cart addToCart(@RequestParam Long productId,
                          @RequestParam int quantity,
                          Principal principal) {

        String username = principal.getName();

        return service.addToCart(username, productId, quantity);
        
        
    }
    
    @GetMapping
    public Cart getCart(Principal principal) {

        String username = principal.getName();

        return service.getCartByUsername(username);
    }
}
