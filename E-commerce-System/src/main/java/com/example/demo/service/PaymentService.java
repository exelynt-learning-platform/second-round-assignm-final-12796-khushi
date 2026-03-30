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

    // ✅ MAIN LOGIC
    @Transactional
    public Payment processPayment(Order order, String method) {

        if (order == null) {
            throw new RuntimeException("Invalid order");
        }

        // ✅ prevent duplicate payment
        Payment existing = paymentRepo.findByOrder(order);
        if (existing != null) {
            return existing;
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice());
        payment.setPaymentMethod(method);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("SUCCESS");

        return paymentRepo.save(payment);
    }

    // ✅ THIS METHOD IS REQUIRED (FIX YOUR ERROR)
    public Payment makePayment(Long orderId, String method) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return processPayment(order, method);
    }
}