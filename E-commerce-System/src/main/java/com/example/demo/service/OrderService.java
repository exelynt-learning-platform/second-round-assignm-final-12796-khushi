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

        // ✅ FIX: ensure order items initialized
        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }

        double total = 0;

        for (CartItem item : cart.getItems()) {

            // ✅ FIX: item null check
            if (item == null) {
                continue;
            }

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

        // ✅ SAVE ORDER
        Order savedOrder = orderRepo.save(order);

        // ✅ PAYMENT (SAFE CHECK)
        Payment payment = paymentService.processPayment(savedOrder, "UPI");

        if (payment == null || !"SUCCESS".equals(payment.getStatus())) {
            throw new RuntimeException("Payment failed");
        }

        savedOrder.setStatus("PAID");
        orderRepo.save(savedOrder);

        // ✅ CLEAR CART
        cart.getItems().clear();
        cartRepo.save(cart);

        return savedOrder;
    }

    // ✅ FIX: user validation added
    public List<Order> getOrdersByUser(String username) {

        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return orderRepo.findByUserUsername(username);
    }
}