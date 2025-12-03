package org.example.dto.response;

import lombok.*;
import org.example.entity.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long id;

    private String customerName;

    private String customerEmail;

    private LocalDateTime orderDate;

    private OrderStatus status;

    private BigDecimal totalAmount;

    private List<OrderItemResponse> items;

}
