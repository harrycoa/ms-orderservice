package com.pe.appventas.msorderservice.util;

import com.pe.appventas.msorderservice.entities.Order;
import com.pe.appventas.msorderservice.entities.OrderDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RepositoryDataUtils {
    public static Order getMockOrder(long id, String accountId) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setId(id);
        order.setAccountId(accountId);
        order.setTotalAmount(1000d);
        order.setTotalIgv(160d);
        order.setTotalAmountIgv(1160d);
        order.setPaymentStatus(OrderPaymentStatus.APPROVED);
        order.setTransactionDate(new Date());
        order.setCreatedDate(new Date());
        order.setLastUpdateDate(new Date());
        order.setDetails(getMockOrderDetails(order));

        return order;
    }

    public static Order getMockNotPersistentOrder(String accountId, OrderStatus orderStatus) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setAccountId(accountId);
        order.setTotalAmount(1000d);
        order.setStatus(orderStatus);
        order.setTotalIgv(160d);
        order.setTotalAmountIgv(1160d);
        order.setPaymentStatus(OrderPaymentStatus.APPROVED);
        order.setTransactionDate(new Date());
        order.setDetails(getMockOrderDetails(order));
        return order;
    }

    public static List<OrderDetail> getMockOrderDetails(Order order) {
        List<OrderDetail> result = new ArrayList<>();

        OrderDetail orderDetail = OrderDetail.builder()
                .upc("999999999991")
                .quantity(2).price(400d)
                .igv(128d)
                .totalAmount(928d)
                .order(order)
                .build();

        OrderDetail orderDetail02 = OrderDetail.builder()
                .upc("999999999992")
                .quantity(2).price(100d)
                .igv(32d)
                .totalAmount(232d)
                .order(order)
                .build();

        result.add(orderDetail);
        result.add(orderDetail02);

        return result;
    }
}
