package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepo;
    @Autowired private OrderRepository orderRepo;

    // ✅ SAFE PAYMENT METHOD
    @Transactional
    public Payment processPayment(Order order, String method) {

        // ✅ VALIDATION
        if (order == null) {
            throw new RuntimeException("Invalid order");
        }

        if (order.getTotalPrice() <= 0) {
            throw new RuntimeException("Invalid payment amount");
        }

        if (method == null || method.isEmpty()) {
            throw new RuntimeException("Payment method required");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice());
        payment.setPaymentMethod(method);
        payment.setPaymentDate(LocalDateTime.now());

        // ✅ Simulate success
        payment.setStatus("SUCCESS");

        return paymentRepo.save(payment);
    }

    // ✅ OPTIONAL: KEEP THIS ONLY IF CONTROLLER USES IT
    public Payment makePayment(Long orderId, String method) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return processPayment(order, method);
    }
}