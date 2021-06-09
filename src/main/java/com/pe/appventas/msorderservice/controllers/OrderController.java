package com.pe.appventas.msorderservice.controllers;

import com.pe.appventas.msorderservice.dto.OrderRequest;
import com.pe.appventas.msorderservice.dto.OrderResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api
@RestController
public class OrderController {

    @ApiOperation(value = "Lista los pedidos (orders) almacenados", notes="Esta operacion retorna  los pedidos almacenados")
    @GetMapping(value =  "order")
    public ResponseEntity<List<OrderResponse>> findAll(){
        List<OrderResponse> orderList = new ArrayList();

        OrderResponse response = new OrderResponse();
        response.setAccountId("00001");
        response.setOrderId("100");
        response.setStatus("PENDING");
        response.setTotalAmount(1000.00);
        response.setTotalIgv(10.00);
        response.setTransactionDate(new Date());


        OrderResponse response2 = new OrderResponse();
        response2.setAccountId("00001");
        response2.setOrderId("101");
        response2.setStatus("PENDING");
        response2.setTotalAmount(1000.00);
        response2.setTotalIgv(10.00);
        response2.setTransactionDate(new Date());

        orderList.add(response);
        orderList.add(response2);

        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @ApiOperation(value = "Lista el pedido (orders)  que se ha buscado por id", notes="Esta operacion retorna el pedido buscado por id")
    @GetMapping(value = "order/{orderId}")
    public ResponseEntity<OrderResponse> findById(@PathVariable String orderId) {
        OrderResponse response2 = new OrderResponse();
        response2.setAccountId("00001");
        response2.setOrderId(orderId);
        response2.setStatus("PENDING");
        response2.setTotalAmount(1000.00);
        response2.setTotalIgv(10.00);
        response2.setTransactionDate(new Date());


        return new ResponseEntity<>(response2, HttpStatus.OK);
    }

    @ApiOperation(value = "Crea un nuevo pedido (order) ", notes="Esta operacion crea un nuevo pedido")
    @PostMapping(value = "order/create")
    public ResponseEntity<OrderResponse>  createOrder(@RequestBody OrderRequest payload){
        OrderResponse response2 = new OrderResponse();
        response2.setAccountId(payload.getAccountId());
        response2.setOrderId("999");
        response2.setStatus("PENDING");
        response2.setTotalAmount(1000.00);
        response2.setTotalIgv(10.00);
        response2.setTransactionDate(new Date());

        return new ResponseEntity<>(response2, HttpStatus.CREATED);
    }
}
