package com.pe.appventas.msorderservice.dto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentDetailsDto {
    private String cardNumber;
    private String nameOnCard;
    private String cardCode;
    private String expirationMonth;
    private String expirationYear;
    private String method;
}
