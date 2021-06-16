package com.pe.appventas.msorderservice.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Order {
    private String orderId;
    private String status;
    private String accountId;
    private Double totalAmount;
    private Double totalIgv;
    private Date transactionDate;
}
