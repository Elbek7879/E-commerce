package org.example.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotBlank
    private String customerName;

    @NotBlank
    @Email
    private String customerEmail;

    @NotEmpty
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }

}
