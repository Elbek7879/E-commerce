package org.example.controller;

import org.example.dto.request.CreateOrderRequest;
import org.example.dto.response.OrderResponse;
import org.example.entity.enums.OrderStatus;
import org.example.service.OrderService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @GetMapping
    public List<OrderResponse> getAll() {
        return service.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse getOne(@PathVariable Long id) {
        return service.getOrderById(id);
    }

    @PostMapping
    public OrderResponse create(@RequestBody @Valid CreateOrderRequest request) {
        return service.createOrder(request);
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteOrder(id);
    }

    @GetMapping("/customer/{email}")
    public List<OrderResponse> byCustomer(@PathVariable String email) {
        return service.getOrdersByCustomerEmail(email);
    }


}
