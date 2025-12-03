package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
