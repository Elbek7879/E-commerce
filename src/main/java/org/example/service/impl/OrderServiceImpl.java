package org.example.service.impl;

import org.example.dto.request.CreateOrderRequest;
import org.example.dto.response.*;
import org.example.entity.*;
import org.example.entity.enums.OrderStatus;
import org.example.exception.*;
import org.example.repository.*;
import org.example.service.OrderService;
import org.example.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final OrderItemRepository itemRepo;

    public OrderServiceImpl(OrderRepository orderRepo, ProductRepository productRepo, OrderItemRepository itemRepo) {
        this.orderRepo = orderRepo; this.productRepo = productRepo; this.itemRepo = itemRepo;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return toResponse(findById(id));
    }


    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1) Validation
        if (!EmailValidator.isValid(request.getCustomerEmail())) {
            throw new InvalidOrderStatusException("Invalid customer email format");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOrderStatusException("Order must contain at least one item");
        }

        Set<Long> productIds = new HashSet<>();
        for (var it : request.getItems()) {
            if (!productIds.add(it.getProductId())) {
                throw new InvalidOrderStatusException("Duplicate product in one order is not allowed: " + it.getProductId());
            }
        }
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        for (var reqItem : request.getItems()) {
            Product product = productRepo.findById(reqItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(reqItem.getProductId()));
            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new InvalidOrderStatusException("Product is inactive: " + product.getId());
            }
            if (product.getStock() == null || product.getStock() <= 0) {
                throw new InsufficientStockException(product.getId());
            }
            if (product.getStock() < reqItem.getQuantity()) {
                throw new InsufficientStockException(product.getId());
            }
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(reqItem.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(reqItem.getQuantity())));
            items.add(item);
        }
        order.setOrderItems(items);
        order.setTotalAmount(PriceCalculator.sumItems(items));

        Order saved = orderRepo.save(order);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = findById(id);

        if (order.getStatus() != OrderStatus.PENDING && newStatus != OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException("Only PENDING orders can be modified");
        }

        if (newStatus == OrderStatus.CONFIRMED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                int newStock = product.getStock() - item.getQuantity();
                if (newStock < 0) {
                    throw new InsufficientStockException(product.getId());
                }
                product.setStock(newStock);
                productRepo.save(product);
            }
        }
        if (newStatus == OrderStatus.CANCELLED) {
            // If already delivered or shipped, we do not restock.
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepo.save(product);
                }
            }
        }

        order.setStatus(newStatus);
        return toResponse(orderRepo.save(order));
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepo.save(product);
            }
        }
        orderRepo.delete(order);
    }

    @Override
    public List<OrderResponse> getOrdersByCustomerEmail(String email) {
        return orderRepo.findByCustomerEmail(email).stream().map(this::toResponse).toList();
    }

    private Order findById(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(order.getOrderItems().stream().map(oi -> OrderItemResponse.builder()
                        .id(oi.getId())
                        .productId(oi.getProduct().getId())
                        .productName(oi.getProduct().getName())
                        .quantity(oi.getQuantity())
                        .unitPrice(oi.getUnitPrice())
                        .totalPrice(oi.getTotalPrice())
                        .build()).toList())
                .build();
    }
}
