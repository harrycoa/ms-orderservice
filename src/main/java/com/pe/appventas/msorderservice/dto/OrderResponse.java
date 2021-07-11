package com.pe.appventas.msorderservice.dto;

import com.pe.appventas.msorderservice.entities.OrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private String orderId;
    private String status;
    private String accountId;
    private Double totalAmount;
    private Double totalIgv;
    private Double TotalAmountIgv;
    private Date transactionDate;
    List<OrderDetailResponse> details;

}
