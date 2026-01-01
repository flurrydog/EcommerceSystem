package org.monicandy.ecommerceapp.service;

import org.monicandy.ecommerceapp.entity.CartItem;
import org.monicandy.ecommerceapp.entity.Product;
import org.monicandy.ecommerceapp.repository.CartItemRepository;
import org.monicandy.ecommerceapp.repository.ProductRepository;
import org.monicandy.ecommerceapp.view.CartPageView;
import org.monicandy.ecommerceapp.view.CartView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    // 删除这个常量：private static final Long MOCK_USER_ID = 1L;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addToCart(Long userId, Long productId) {  // 添加 userId 参数
        CartItem item = cartItemRepository
                .findByUserIdAndProductId(userId, productId)  // 使用传入的 userId
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUserId(userId);  // 使用传入的 userId
                    newItem.setProductId(productId);
                    newItem.setQuantity(0);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(item);
    }

    public List<CartItem> listCartItems(Long userId) {  // 添加 userId 参数
        return cartItemRepository.findByUserId(userId);  // 使用传入的 userId
    }

    public CartPageView getCartPageView(Long userId) {  // 添加 userId 参数
        List<CartItem> items = cartItemRepository.findByUserId(userId);  // 使用传入的 userId

        List<CartView> views = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found: " + item.getProductId()));

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            views.add(new CartView(
                    product.getName(),
                    product.getPrice(),
                    item.getQuantity(),
                    subtotal
            ));

            total = total.add(subtotal);
        }

        return new CartPageView(views, total);
    }
}
