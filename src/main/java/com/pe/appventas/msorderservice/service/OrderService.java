package com.pe.appventas.msorderservice.service;

import com.pe.appventas.msorderservice.client.CustomerServiceClient;
import com.pe.appventas.msorderservice.client.InventoryServiceClient;
import com.pe.appventas.msorderservice.dao.JpaOrderDAO;
import com.pe.appventas.msorderservice.dto.AccountDto;
import com.pe.appventas.msorderservice.dto.Confirmation;
import com.pe.appventas.msorderservice.dto.OrderRequest;
import com.pe.appventas.msorderservice.dto.ShipmentOrderResponse;
import com.pe.appventas.msorderservice.entities.Order;
import com.pe.appventas.msorderservice.entities.OrderDetail;
import com.pe.appventas.msorderservice.exception.AccountNotFoundException;
import com.pe.appventas.msorderservice.exception.OrderNotFoundException;
import com.pe.appventas.msorderservice.exception.PaymentNotAcceptedException;
import com.pe.appventas.msorderservice.producer.ShippingOrderProducer;
import com.pe.appventas.msorderservice.repositories.OrderRepository;
import com.pe.appventas.msorderservice.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private CustomerServiceClient customerClient;

    @Autowired
    private JpaOrderDAO orderDAO;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentProcessorService paymentService;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    private ShippingOrderProducer shippingMessageProducer;


    // necesita un contexto transaccional
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Order createOrder(OrderRequest orderRequest) throws PaymentNotAcceptedException {
        // Validamos la excepcion de order
        OrderValidator.validateOrder(orderRequest);
        AccountDto account = customerClient
                .findAccountById(orderRequest.getAccountId()).orElseThrow(()-> new AccountNotFoundException(ExceptionMessagesEnum.ACCOUNT_NOT_FOUND.getValue()));
        Order newOrder = initOrder(orderRequest);

        Confirmation confirmation = paymentService.processPayment(newOrder, account);

        log.info("Payment Confirmation: {}", confirmation);

        String paymentStatus = confirmation.getTransactionStatus();
        newOrder.setPaymentStatus(OrderPaymentStatus.valueOf(paymentStatus));

        if (paymentStatus.equals(OrderPaymentStatus.DENIED.name())) {
            newOrder.setStatus(OrderStatus.NA);
            orderRepository.save(newOrder);
            throw new PaymentNotAcceptedException("El Pago  de su cuenta no fue aceptado, por favor verifique.");
        }

        log.info("Actualizamos el inventario: {}", orderRequest.getItems());
        inventoryServiceClient.updateInventory(orderRequest.getItems());

        log.info("Enviamos el request al ShippingService", orderRequest.getItems());
        shippingMessageProducer.send(newOrder.getOrderId(), account);

        return orderRepository.save(newOrder);
    }

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
                        .totalAmount((item.getPrice() * item.getQuantity()))
                        .order(orderObj).build())
                .collect(Collectors.toList());

        orderObj.setDetails(orderDetails);
        orderObj.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getTotalAmount).sum());
        orderObj.setTotalIgv(orderObj.getTotalAmount() * Constants.IGV_IMPORT);
        orderObj.setTotalAmountIgv(orderObj.getTotalAmount() + orderObj.getTotalIgv());
        orderObj.setTransactionDate(new Date());
        return orderObj;

    }

    // Spring Data
    public List<Order> findAllOrder(){
        return orderRepository.findAll();

    }
    public Order findOrderById(String orderId){
        Optional<Order> order = Optional.ofNullable(orderRepository.findOrderByOrderId(orderId));
        return order
                .orElseThrow(() -> new OrderNotFoundException("Order no encontrada"));
    }

    public Order findById(Long id){
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order no encontrada"));
    }
    public List<Order> findOrdersByAccountId(String accountId){
        Optional<List<Order>> orders = Optional.ofNullable(orderRepository.findOrderByAccountId(accountId));
        return orders
                .orElseThrow(() -> new OrderNotFoundException("Orders no fueron encontradas"));
    }

    public void updateShipmentOrder(ShipmentOrderResponse response){
        try {
            Order order = findOrderById(response.getOrderId());
            order.setStatus(OrderStatus.valueOf(response.getShippingStatus()));
            orderRepository.save(order);
        } catch (OrderNotFoundException orderNotFound) {
            log.info(" el siguiente pedido no fue encontrado: {} con el tracking : {}", response.getOrderId(), response.getTrackingId());
        }  catch (Exception e) {
            log.info("Ocurrio un error");
        }
    }

}
