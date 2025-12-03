package org.example.dto.response;


import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {

    private Long id;

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

}
