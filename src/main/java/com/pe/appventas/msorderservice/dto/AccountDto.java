package com.pe.appventas.msorderservice.dto;

import com.pe.appventas.msorderservice.util.AccountStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountDto {
    private Long id;
    private AddressDto address;
    private CustomerDto customer;
    private CreditCardDto creditCard;
    private AccountStatus accountStatus;

}
