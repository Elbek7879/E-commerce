package org.example.util;

import org.example.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public class PriceCalculator {

    public static BigDecimal sumItems(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private PriceCalculator() {}

}
