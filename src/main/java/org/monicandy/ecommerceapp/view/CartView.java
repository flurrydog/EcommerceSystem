package org.monicandy.ecommerceapp.view;

import java.math.BigDecimal;

public record CartView(
        String name,
        BigDecimal price,
        int quantity,
        BigDecimal subtotal
) {}

