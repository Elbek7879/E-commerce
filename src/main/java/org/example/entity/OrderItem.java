package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false) @JoinColumn(name = "product_id")
    private Product product;

    @NotNull @Positive
    private Integer quantity;

    @NotNull
    private BigDecimal unitPrice;

    @NotNull
    private BigDecimal totalPrice;

}
