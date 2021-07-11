package com.pe.appventas.msorderservice.dto;

import lombok.Data;

@Data
public class OrderDetailResponse {
    private long id;
    private int quantity;
    private double price;
    private double igv;
    private String upc;
    private Double totalAmount;
}
