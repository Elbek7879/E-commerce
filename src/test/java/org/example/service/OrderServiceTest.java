package org.example.service;

import org.example.dto.request.CreateOrderRequest;
import org.example.dto.response.OrderResponse;
import org.example.entity.*;
import org.example.entity.enums.OrderStatus;
import org.example.exception.*;
import org.example.repository.*;
import org.example.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private OrderRepository orderRepo;
    private ProductRepository productRepo;
    private OrderItemRepository itemRepo;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderRepo = mock(OrderRepository.class);
        productRepo = mock(ProductRepository.class);
        itemRepo = mock(OrderItemRepository.class);
        orderService = new OrderServiceImpl(orderRepo, productRepo, itemRepo);
    }

    @Test
    void testCreateOrder_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStock(10);
        product.setIsActive(true);

        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");
        request.setItems(List.of(itemRequest));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setCustomerName("John Doe");
        savedOrder.setCustomerEmail("john@example.com");
        savedOrder.setOrderDate(LocalDateTime.now());
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setTotalAmount(BigDecimal.valueOf(2000));

        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepo, times(1)).save(orderCaptor.capture());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerName()).isEqualTo("John Doe");
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(1);
        product.setIsActive(true);

        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");
        request.setItems(List.of(itemRequest));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(request));
    }

    @Test
    void testCreateOrder_InvalidEmail() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("John Doe");
        request.setCustomerEmail("invalid-email");
        request.setItems(List.of());

        assertThrows(InvalidOrderStatusException.class, () -> orderService.createOrder(request));
    }

    @Test
    void testGetOrderById_Found() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("John Doe");

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);
        assertThat(response.getCustomerName()).isEqualTo("John Doe");
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepo.findById(100L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(100L));
    }

    @Test
    void testUpdateStatus_PendingToConfirmed() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(5);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(List.of(item));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(order)).thenReturn(order);
        when(productRepo.save(product)).thenReturn(product);

        OrderResponse response = orderService.updateStatus(1L, OrderStatus.CONFIRMED);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(product.getStock()).isEqualTo(3);
    }

    @Test
    void testUpdateStatus_NotPending() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.SHIPPED);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () ->
                orderService.updateStatus(1L, OrderStatus.CONFIRMED));
    }

    @Test
    void testDeleteOrder_Restocks() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(5);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(List.of(item));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        doNothing().when(orderRepo).delete(order);
        when(productRepo.save(product)).thenReturn(product);

        orderService.deleteOrder(1L);

        assertThat(product.getStock()).isEqualTo(7);
        verify(orderRepo, times(1)).delete(order);
    }

    @Test
    void testGetOrdersByCustomerEmail() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setCustomerEmail("john@example.com");

        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomerEmail("john@example.com");

        when(orderRepo.findByCustomerEmail("john@example.com")).thenReturn(List.of(order1, order2));

        List<OrderResponse> result = orderService.getOrdersByCustomerEmail("john@example.com");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCustomerEmail()).isEqualTo("john@example.com");
    }

}
