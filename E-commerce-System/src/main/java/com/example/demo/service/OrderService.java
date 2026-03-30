package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepo;
    @Autowired private CartRepository cartRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private PaymentService paymentService;

    @Transactional
    public Order placeOrder(String username) {

        // ✅ USER VALIDATION
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ CART VALIDATION
        Cart cart = cartRepo.findByUser(user);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setItems(new ArrayList<>());

        double total = 0;

        // ✅ PREPARE ORDER (NO STOCK CHANGE YET)
        for (CartItem item : cart.getItems()) {

            if (item == null) continue;

            Product product = item.getProduct();

            if (product == null) {
                throw new RuntimeException("Invalid product in cart");
            }

            if (item.getQuantity() <= 0) {
                throw new RuntimeException("Invalid quantity");
            }

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Out of stock: " + product.getName());
            }

            OrderItem oi = new OrderItem();
            oi.setProduct(product);
            oi.setQuantity(item.getQuantity());
            oi.setPrice(product.getPrice());
            oi.setOrder(order);

            total += oi.getPrice() * oi.getQuantity();
            order.getItems().add(oi);
        }

        order.setTotalPrice(total);

        // ✅ SAVE ORDER FIRST
        Order savedOrder = orderRepo.save(order);

        // ✅ PROCESS PAYMENT
        Payment payment = paymentService.processPayment(savedOrder, "UPI");

        if (payment == null || !"SUCCESS".equals(payment.getStatus())) {
            throw new RuntimeException("Payment failed");
        }

        // ✅ REDUCE STOCK AFTER PAYMENT SUCCESS
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepo.save(product);
        }

        // ✅ UPDATE STATUS (NO EXTRA SAVE NEEDED)
        savedOrder.setStatus("PAID");

        // ✅ CLEAR CART
        cart.getItems().clear();
        cartRepo.save(cart);

        return savedOrder;
    }

    // ✅ GET USER ORDERS
    public List<Order> getOrdersByUser(String username) {

        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return orderRepo.findByUserUsername(username);
    }
}