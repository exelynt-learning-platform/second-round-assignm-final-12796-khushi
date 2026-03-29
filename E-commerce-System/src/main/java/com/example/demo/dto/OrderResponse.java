package com.example.demo.dto;

public class OrderResponse {

    private Long orderId;
    private double totalPrice;
    private String status;

    public OrderResponse(Long orderId, double totalPrice, String status) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }
}