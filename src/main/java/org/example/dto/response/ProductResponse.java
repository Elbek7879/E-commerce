package org.example.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private BigDecimal price;

    private Integer stock;

    private String category;

    private Boolean isActive;

    private LocalDateTime createdAt;

}
