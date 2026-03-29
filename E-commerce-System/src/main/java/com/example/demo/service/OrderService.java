package com.example.demo.service;


import com.example.demo.entity.*;
import com.example.demo.repository.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepo;
    @Autowired private CartRepository cartRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ProductRepository productRepo; // ✅ ADD

    // ✅ PLACE ORDER
    public Order placeOrder(String username) {

        User user = userRepo.findByUsername(username);
        Cart cart = cartRepo.findByUser(user);

        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);

        double total = 0;

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            // 🔥 1. STOCK VALIDATION
            if (item.getProduct().getStock() < item.getQuantity()) {
                throw new RuntimeException("Out of stock");
            }
            // 🔥 2. REDUCE STOCK
            product.setStock(product.getStock() - item.getQuantity());
            productRepo.save(product);

            OrderItem oi = new OrderItem();
            oi.setProduct(product);
            oi.setQuantity(item.getQuantity());
            oi.setPrice(product.getPrice());

            oi.setOrder(order); // ✅ IMPORTANT

            total += oi.getPrice() * oi.getQuantity();

            order.getItems().add(oi);
        }

        order.setTotalPrice(total);
        order.setStatus("PENDING");

        Order savedOrder = orderRepo.save(order);

        // 🔥 3. CLEAR CART AFTER ORDER
        cart.getItems().clear();
        cartRepo.save(cart);

        return savedOrder;
    }

    // ❌ OLD METHOD (NOT SAFE)
    // public List<Order> getAllOrders()

    // ✅ FIXED: USER-SPECIFIC ORDERS
    public List<Order> getOrdersByUser(String username) {
        return orderRepo.findByUserUsername(username);
    }
}