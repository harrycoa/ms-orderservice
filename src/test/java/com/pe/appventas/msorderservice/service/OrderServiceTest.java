package com.pe.appventas.msorderservice.service;

import com.pe.appventas.msorderservice.client.CustomerServiceClient;
import com.pe.appventas.msorderservice.client.InventoryServiceClient;
import com.pe.appventas.msorderservice.dto.AccountDto;
import com.pe.appventas.msorderservice.dto.OrderRequest;
import com.pe.appventas.msorderservice.entities.Order;
import com.pe.appventas.msorderservice.exception.AccountNotFoundException;
import com.pe.appventas.msorderservice.exception.IncorrectOrderRequestException;
import com.pe.appventas.msorderservice.exception.PaymentNotAcceptedException;
import com.pe.appventas.msorderservice.producer.ShippingOrderProducer;
import com.pe.appventas.msorderservice.repositories.OrderRepository;
import com.pe.appventas.msorderservice.util.ExceptionMessagesEnum;
import com.pe.appventas.msorderservice.util.OrderPaymentStatus;
import com.pe.appventas.msorderservice.util.OrderServiceDataTestUtils;
import com.pe.appventas.msorderservice.util.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;


@ExtendWith(SpringExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private CustomerServiceClient customerClient;

    @Mock
    private PaymentProcessorService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryServiceClient inventoryClient;

    @Mock
    private ShippingOrderProducer shipmentMessageProducer;

    // Ejm
    //Should_ExpectedBehavior_When_StateUnderTest
    //Should_ThrowException_When_AgeLessThan25
    //Should_FailToWithdrawMoney_ForInvalidAccount
    //Should_FailToAdmit_IfMandatoryFieldsAreMissing

    @BeforeEach
    public void init() {
        AccountDto mockAccount = OrderServiceDataTestUtils.getMockAccount("12345678");
        Mockito.doReturn(Optional.of(mockAccount)).when(customerClient).findAccountById(anyString());
    }

    @DisplayName("Deberia lanzar una excepción incorrecta cuando los elementos del pedido son nulos")
    @Test
    public void shouldThrowIncorrectExceptionWhenOrderItemsAreNull(){
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAccountId("12345678");

        IncorrectOrderRequestException incorrectExceptions = Assertions.assertThrows(IncorrectOrderRequestException.class,
                () -> orderService.createOrder(orderRequest));

        Assertions.assertEquals(ExceptionMessagesEnum.INCORRECT_REQUEST_EMPTY_ITEMS_ORDER.getValue(), incorrectExceptions.getMessage());
    }

    @DisplayName("Deberia lanzar una excepción incorrecta cuando los elementos del pedido son vacios")
    @Test
    public void shouldThrowIncorrectExceptionWhenOrderItemsAreEmpty() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAccountId("12345678");
        orderRequest.setItems(new ArrayList<>());

        IncorrectOrderRequestException incorrectExceptions = Assertions.assertThrows(IncorrectOrderRequestException.class,
                () -> orderService.createOrder(orderRequest));

        Assertions.assertEquals(ExceptionMessagesEnum.INCORRECT_REQUEST_EMPTY_ITEMS_ORDER.getValue(), incorrectExceptions.getMessage());
    }

    @DisplayName("Deberia lanzar una excepción de cuenta no encontrada cuando la cuenta no existe")
    @Test
    public void shouldThrowAccountNotFoundWhenAccountDoesNotExists() {
        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("12345678");

        Mockito.when(customerClient.findAccountById(anyString())).thenReturn(Optional.empty());

        AccountNotFoundException accountNotFoundException = Assertions.assertThrows(AccountNotFoundException.class,
                () -> orderService.createOrder(orderRequest));

        Assertions.assertEquals(ExceptionMessagesEnum.ACCOUNT_NOT_FOUND.getValue(), accountNotFoundException.getMessage());
        // Mockito.verify(customerClient, Mockito.times(1)).findAccountById(anyString());
        Mockito.verify(customerClient).findAccountById(anyString());
    }

    @DisplayName("Deberia arrojar la excepción de pago no aceptado cuando se niega el pago")
    @Test
    public void shouldThrowPaymentNotAcceptedExceptionWhenPaymentIsDenied() {
        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("12345678");

        AccountDto mockAccount = OrderServiceDataTestUtils.getMockAccount(orderRequest.getAccountId());

        Mockito.doReturn(Optional.of(mockAccount)).when(customerClient).findAccountById(anyString());

        Mockito.when(paymentService.processPayment(any(), any())).thenReturn(OrderServiceDataTestUtils
                .getMockPayment(orderRequest.getAccountId(), OrderPaymentStatus.DENIED));

        Mockito.doReturn(new Order()).when(orderRepository).save(any(Order.class));

        PaymentNotAcceptedException paymentNotAcceptedException = Assertions.assertThrows(PaymentNotAcceptedException.class,
                () -> orderService.createOrder(orderRequest));

        Assertions.assertEquals("El Pago  de su cuenta no fue aceptado, por favor verifique.", paymentNotAcceptedException.getMessage());
        Mockito.verify(customerClient).findAccountById(anyString());
        Mockito.verify(orderRepository).save(any(Order.class));
        Mockito.verify(paymentService).processPayment(any(), any());
    }

    @DisplayName("Deberia devolver la orden pendiente cuando se llama a Crear orden")
    @Test
    public void shouldReturnPendingOrderWhenCreateOrderIsCalled() throws PaymentNotAcceptedException {
        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("12345678");

        Mockito.when(paymentService.processPayment(any(), any())).thenReturn(OrderServiceDataTestUtils
                .getMockPayment(orderRequest.getAccountId(), OrderPaymentStatus.APPROVED));

        Mockito.doNothing().when(inventoryClient).updateInventory(anyList());
        Mockito.doNothing().when(shipmentMessageProducer).send(anyString(), any());
        Mockito.when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order order = orderService.createOrder(orderRequest);

        Assertions.assertEquals("12345678", order.getAccountId());
        Assertions.assertEquals(Double.valueOf("1005"), order.getTotalAmount());
        Assertions.assertEquals(Double.valueOf("180.9"), order.getTotalIgv());
        Assertions.assertEquals(Double.valueOf("1185.9"), order.getTotalAmountIgv());
        Assertions.assertEquals(OrderStatus.PENDING, order.getStatus());
        Assertions.assertEquals(2, order.getDetails().size());
        Assertions.assertEquals(OrderPaymentStatus.APPROVED, order.getPaymentStatus());
        Assertions.assertNotNull(order.getTransactionDate());

        /* Otro modo de hacer las preguntas para el test hamcrest (framework)
        assertThat(order.getOrderId(), not(isEmptyString()));
        assertThat(order.getAccountId(), is(Matchers.equalTo("12345678")));
        assertThat(order.getTotalAmount(), is(Matchers.equalTo(1005d)));
        assertThat(order.getTotalTax(), is(Matchers.equalTo(160.8d)));
        assertThat(order.getTotalAmountTax(), is(Matchers.equalTo(1165.8d)));
        assertThat(order.getStatus(), is(Matchers.equalTo(OrderStatus.PENDING)));
        assertThat(order.getDetails().size(), is(Matchers.equalTo(2)));
        assertThat(order.getPaymentStatus(), is(Matchers.equalTo(OrderPaymentStatus.APPROVED)));
        assertThat(order.getTransactionDate(), is(Matchers.notNullValue()));
        */

        Mockito.verify(customerClient).findAccountById(anyString());
        Mockito.verify(paymentService).processPayment(any(), any());
        Mockito.verify(inventoryClient).updateInventory(anyList());
        Mockito.verify(shipmentMessageProducer).send(anyString(), any());
        Mockito.verify(orderRepository).save(any(Order.class));
    }



}
