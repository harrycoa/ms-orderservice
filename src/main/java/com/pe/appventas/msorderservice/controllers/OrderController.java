package com.pe.appventas.msorderservice.controllers;

import com.pe.appventas.msorderservice.dto.OrderRequest;
import com.pe.appventas.msorderservice.dto.OrderResponse;
import com.pe.appventas.msorderservice.entities.Order;
import com.pe.appventas.msorderservice.exception.PaymentNotAcceptedException;
import com.pe.appventas.msorderservice.service.OrderService;
import com.pe.appventas.msorderservice.util.EntityDtoConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EntityDtoConverter converter;

    @ApiOperation(value = "Lista los pedidos (orders) almacenados", notes="Esta operacion retorna  los pedidos almacenados")
    @GetMapping(value =  "order")
    public ResponseEntity<List<OrderResponse>> findAll(){
        List<Order> orders = orderService.findAllOrder();
        return new ResponseEntity<>(converter.convertEntityToDto(orders), HttpStatus.OK);
    }

    @ApiOperation(value = "Lista el pedido (order)  que se ha buscado por id", notes="Esta operacion retorna el pedido buscado por orderId")
    @GetMapping(value = "order/{orderId}")
    public ResponseEntity<OrderResponse> findByOrderId(@PathVariable String orderId) {
        Order order = orderService.findOrderById(orderId);
        return new ResponseEntity<>(converter.convertEntityToDto(order), HttpStatus.OK);
    }

    @ApiOperation(value = "Lista el pedido (order)  que se ha buscado por id", notes="Esta operacion retorna el pedido buscado por dborder id")
    @GetMapping(value = "order/generated/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return new ResponseEntity<>(converter.convertEntityToDto(order), HttpStatus.OK);
    }

    @ApiOperation(value = "Lista los pedidos (orders)  que se ha buscado por id", notes="Esta operacion retorna los pedidos buscado por accountid")
    @GetMapping(value = "order/account/{accountId}")
    public ResponseEntity<List<OrderResponse>> findOrdersByAccountId(@PathVariable String accountId) {
        List<Order> orders = orderService.findOrdersByAccountId(accountId);
        return new ResponseEntity<>(converter.convertEntityToDto(orders), HttpStatus.OK);
    }

    @ApiOperation(value = "Crea un nuevo pedido (order) ", notes="Esta operacion crea un nuevo pedido")
    @PostMapping(value = "order/create")
    public ResponseEntity<OrderResponse>  createOrder(@RequestBody OrderRequest payload) throws PaymentNotAcceptedException {
        Order order = orderService.createOrder(payload);
        return new ResponseEntity<>(converter.convertEntityToDto(order), HttpStatus.CREATED);
    }
}
