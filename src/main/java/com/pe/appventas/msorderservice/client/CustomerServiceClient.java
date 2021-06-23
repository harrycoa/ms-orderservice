package com.pe.appventas.msorderservice.client;

import com.pe.appventas.msorderservice.config.OrderServiceConfig;
import com.pe.appventas.msorderservice.dto.AccountDto;
import com.pe.appventas.msorderservice.dto.AddressDto;
import com.pe.appventas.msorderservice.dto.CreditCardDto;
import com.pe.appventas.msorderservice.dto.CustomerDto;
import com.pe.appventas.msorderservice.util.AccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
public class CustomerServiceClient {

    private RestTemplate restTemplate;

    @Autowired
    private OrderServiceConfig config;

    public CustomerServiceClient(RestTemplateBuilder builder){
        restTemplate = builder.build();
    }

    public Optional<AccountDto> findAccountById(String accountId){
        Optional<AccountDto> result = Optional.empty();
        try {
            result =  Optional.ofNullable(restTemplate.getForObject(config.getCustomerServiceUrl() + "/{id}", AccountDto.class, accountId));
        } catch (HttpClientErrorException ex){
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND){
                throw ex;
            }
        }
          return  result;
    }

    public AccountDto createDummyAccount(){
        AddressDto address = AddressDto.builder()
                                    .street("Av. el sol")
                                    .city("Cusco")
                                    .state("Cusco")
                                    .state("Peru")
                                    .zipCode("51")
                                    .build();

        CustomerDto customer = CustomerDto.builder()
                .lastName("Coa")
                .firstName("Harry")
                .email("harry.coa@xyz.com")
                .build();

        CreditCardDto creditCard = CreditCardDto.builder()
                .nameOnCard("Harry coa")
                .number("4320 1231 4552 1234")
                .expirationMonth("03")
                .expirationYear("2025")
                .type("VISA")
                .ccv("123")
                .build();

        AccountDto account = AccountDto.builder()
                .address(address)
                .customer(customer)
                .creditCard(creditCard)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        return account;


    }

    public AccountDto createAccount(AccountDto account){
        return restTemplate.postForObject(config.getCustomerServiceUrl(),  account, AccountDto.class);
    }

    public AccountDto createAccountBody(AccountDto account){
        ResponseEntity<AccountDto> responseAccount =  restTemplate.postForEntity(config.getCustomerServiceUrl(),  account, AccountDto.class);
        log.info("Response: " + responseAccount.getHeaders());
        return  responseAccount.getBody();
    }

    public void updateAccount(AccountDto account){
        restTemplate.put(config.getCustomerServiceUrl() + "/{id}", account ,account.getId());
    }

    public  void deleteAccount(AccountDto account) {
        restTemplate.delete(config.getCustomerServiceUrl() + "/{id}", account.getId());
    }
}
