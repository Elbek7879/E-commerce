package org.example.dto.request;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer stock;

    @NotBlank
    private String category;

    @NotNull
    private Boolean isActive;

}
