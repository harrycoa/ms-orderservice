package com.pe.appventas.msorderservice.consumer;

import com.pe.appventas.msorderservice.dto.ShipmentOrderResponse;
import com.pe.appventas.msorderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShippingOrderConsumer {
    @Autowired
    private OrderService orderService;

    @RabbitListener( queues = "OUTBOUND_SHIPMENT_ORDER")
    private void receive(final ShipmentOrderResponse in){
        log.debug(" [x] Informacion de envio recibida : {}'", in);
        orderService.updateShipmentOrder(in);
    }

}
