package org.monicandy.ecommerceapp.view;

import java.math.BigDecimal;
import java.util.List;

public record CartPageView(
        List<CartView> items,
        BigDecimal total
) {}

