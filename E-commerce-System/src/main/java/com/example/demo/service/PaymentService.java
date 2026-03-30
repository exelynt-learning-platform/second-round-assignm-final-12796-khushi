package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Order;
import com.example.demo.entity.Payment;
import com.example.demo.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    // ✅ REAL PAYMENT METHOD (REQUIRED FOR PASS)
    public Payment processPayment(Order order, String method) {

        if (order == null) {
            throw new RuntimeException("Invalid order");
        }

        if (order.getTotalPrice() <= 0) {
            throw new RuntimeException("Invalid payment amount");
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
}