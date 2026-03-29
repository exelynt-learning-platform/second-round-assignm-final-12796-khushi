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
            OrderItem oi = new OrderItem();

            oi.setProduct(item.getProduct());
            oi.setQuantity(item.getQuantity());
            oi.setPrice(item.getProduct().getPrice());

            oi.setOrder(order);   // 🔥 MUST ADD THIS

            total += oi.getPrice() * oi.getQuantity();
            order.getItems().add(oi);
        }

        order.setTotalPrice(total);
        order.setStatus("PENDING");

        return orderRepo.save(order);
    }

	public List<Order> getAllOrders() {
    return orderRepo.findAll();
}
}
