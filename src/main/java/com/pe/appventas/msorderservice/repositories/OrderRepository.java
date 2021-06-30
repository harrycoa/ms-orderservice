package com.pe.appventas.msorderservice.repositories;

import com.pe.appventas.msorderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findOrderByAccountId(String accountId);
    public Order findOrderByOrderId(String orderId);
}
