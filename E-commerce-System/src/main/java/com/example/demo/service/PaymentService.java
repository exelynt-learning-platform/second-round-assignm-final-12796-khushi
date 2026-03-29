package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepo;
    @Autowired private OrderRepository orderRepo;

    public Payment makePayment(Long orderId, String method) {

        Order order = orderRepo.findById(orderId).orElseThrow();

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice());
        payment.setPaymentMethod(method);
        payment.setPaymentDate(LocalDateTime.now());

        // 🔥 Fake success logic
        payment.setStatus("SUCCESS");

        // ✅ Update order status
        order.setStatus("PAID");
        orderRepo.save(order);

        return paymentRepo.save(payment);
    }
}