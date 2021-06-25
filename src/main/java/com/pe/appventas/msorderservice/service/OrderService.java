package com.pe.appventas.msorderservice.service;

import com.pe.appventas.msorderservice.client.CustomerServiceClient;
import com.pe.appventas.msorderservice.dao.JpaOrderDAO;
import com.pe.appventas.msorderservice.dto.AccountDto;
import com.pe.appventas.msorderservice.dto.OrderRequest;
import com.pe.appventas.msorderservice.entities.Order;
import com.pe.appventas.msorderservice.entities.OrderDetail;
import com.pe.appventas.msorderservice.exception.AccountNotFoundException;
import com.pe.appventas.msorderservice.exception.OrderNotFoundException;
import com.pe.appventas.msorderservice.util.Constants;
import com.pe.appventas.msorderservice.util.ExceptionMessagesEnum;
import com.pe.appventas.msorderservice.util.OrderStatus;
import com.pe.appventas.msorderservice.util.OrderValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private CustomerServiceClient customerClient;

    @Autowired
    private JpaOrderDAO orderDAO;


    private Order initOrder(OrderRequest orderRequest){
        Order orderObj = new Order();
        orderObj.setOrderId(UUID.randomUUID().toString());
        orderObj.setAccountId(orderRequest.getAccountId());
        orderObj.setStatus(OrderStatus.PENDING);

        // lista de orderdetail
        List<OrderDetail> orderDetails = orderRequest.getItems().stream()
                .map(item -> OrderDetail.builder()
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .upc(item.getUpc())
                        .igv((item.getPrice() * item.getQuantity()) * Constants.IGV_IMPORT)
                        .order(orderObj).build())
                .collect(Collectors.toList());

        orderObj.setDetails(orderDetails);
        orderObj.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getPrice).sum());
        orderObj.setTotalIgv(orderObj.getTotalAmount() * Constants.IGV_IMPORT);
        orderObj.setTransactionDate(new Date());
        return orderObj;

    }
    // necesita un contexto transaccional
    @Transactional
    public Order createOrder(OrderRequest orderRequest){

        // Validamos la excepcion de order
        OrderValidator.validateOrder(orderRequest);
        AccountDto account = customerClient.findAccountById(orderRequest.getAccountId())
                                                                        .orElseThrow(()-> new AccountNotFoundException(ExceptionMessagesEnum.ACCOUNT_NOT_FOUND.getValue()));


        Order newOrder = initOrder(orderRequest);
        return orderDAO.save(newOrder);

    }

    public List<Order> findAllOrder(){
        return orderDAO.findAll();

    }

    public Order findOrderById(String orderId){
        return orderDAO.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order no encontrada"));
    }

    public Order findById(Long id){
        return orderDAO.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order no encontrada"));
    }

}
