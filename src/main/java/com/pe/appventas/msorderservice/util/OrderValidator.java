package com.pe.appventas.msorderservice.util;

import com.pe.appventas.msorderservice.dto.OrderRequest;
import com.pe.appventas.msorderservice.exception.IncorrectOrderRequestException;

public class OrderValidator {
    public static boolean validateOrder(OrderRequest order){
        if (order.getItems() == null || order.getItems().isEmpty()){
            throw new IncorrectOrderRequestException(ExceptionMessagesEnum.INCORRECT_REQUEST_EMPTY_ITEMS_ORDER.getValue());
        }
        return true;
    }
}
