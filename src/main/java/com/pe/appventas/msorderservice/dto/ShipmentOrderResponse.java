package com.pe.appventas.msorderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShipmentOrderResponse implements Serializable {
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("receiptEmail")
    private String receiptEmail;

    @JsonProperty("trackingId")
    private String trackingId;

    @JsonProperty("shippingStatus")
    private String shippingStatus;

    @JsonProperty("address")
    private AddressDto address;

    @JsonProperty("shippingDate")
    private Date shippingDate;

    @JsonProperty("deliveredDate")
    private Date deliveredDate;
}
