package org.example.service;

import org.example.dto.request.CreateOrderRequest;
import org.example.dto.response.OrderResponse;
import org.example.entity.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse updateStatus(Long id, OrderStatus newStatus);
    void deleteOrder(Long id);
    List<OrderResponse> getOrdersByCustomerEmail(String email);

}
