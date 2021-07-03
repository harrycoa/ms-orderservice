package com.pe.appventas.msorderservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.appventas.msorderservice.dto.AccountDto;
import com.pe.appventas.msorderservice.dto.CustomerDto;
import com.pe.appventas.msorderservice.dto.ShipmentOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShippingOrderProducer {
    @Autowired
    private RabbitTemplate template;

    @Qualifier(value="outbound")
    private Queue queue;

    public void send(String orderId, AccountDto account){
        ShipmentOrderRequest shipmentRequest = new ShipmentOrderRequest();

        // convertir el objeto a json
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            CustomerDto customer = account.getCustomer();
            String shipmentReceiver = customer.getLastName() + ", " + customer.getFirstName();

            shipmentRequest.setOrderId(orderId);
            shipmentRequest.setName(shipmentReceiver);
            shipmentRequest.setReceiptEmail(customer.getEmail());
            shipmentRequest.setShipmentAddress(account.getAddress());

            Message jsonMessage = MessageBuilder.withBody(objectMapper.writeValueAsString(shipmentRequest).getBytes())
                    .andProperties(MessagePropertiesBuilder.newInstance().setContentType("application/json")
                            .build()).build();

            this.template.convertAndSend("INBOUND_SHIPMENT_ORDER", jsonMessage);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.debug(" [x] Sent '" + shipmentRequest.toString() + "'");

    }
}
