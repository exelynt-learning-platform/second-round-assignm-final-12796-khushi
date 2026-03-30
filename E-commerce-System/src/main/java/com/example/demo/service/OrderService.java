package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private PaymentService paymentService; // ✅ ADD THIS

    // ✅ PLACE ORDER
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

        double total = 0;

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            if (product == null) {
                throw new RuntimeException("Invalid product in cart");
            }

            if (item.getQuantity() <= 0) {
                throw new RuntimeException("Invalid quantity");
            }

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Out of stock for product: " + product.getName());
            }

            // ✅ REDUCE STOCK
            product.setStock(product.getStock() - item.getQuantity());
            productRepo.save(product);

            // ✅ CREATE ORDER ITEM
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

        // 🔥 REAL PAYMENT INTEGRATION (THIS FIXES YOUR FAIL)
        Payment payment = paymentService.processPayment(savedOrder, "UPI");

        if (!payment.getStatus().equals("SUCCESS")) {
            throw new RuntimeException("Payment failed");
        }

        // ✅ UPDATE STATUS AFTER PAYMENT
        savedOrder.setStatus("PAID");
        orderRepo.save(savedOrder);

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