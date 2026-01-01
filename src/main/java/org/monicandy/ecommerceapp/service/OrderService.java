package org.monicandy.ecommerceapp.service;

import org.monicandy.ecommerceapp.entity.*;
import org.monicandy.ecommerceapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrderFromCart(Long userId) {
        // 1. 获取用户的购物车商品
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空，无法创建订单");
        }

        // 2. 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal price = product.getPrice();
                BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());
                totalAmount = totalAmount.add(price.multiply(quantity));
            }
        }

        // 3. 创建订单主表
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("待付款");
        order.setCreateTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 4. 创建订单明细并更新库存
        for (CartItem cartItem : cartItems) {
            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();

                // 创建订单项
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(savedOrder.getId());
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setPrice(product.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());

                // 计算小计
                BigDecimal subtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                orderItem.setSubtotal(subtotal);

                orderItemRepository.save(orderItem);

                // 更新商品库存
                int newStock = product.getStock() - cartItem.getQuantity();
                if (newStock < 0) {
                    throw new RuntimeException("商品库存不足: " + product.getName());
                }
                product.setStock(newStock);
                productRepository.save(product);
            }
        }

        // 5. 清空购物车
        cartItemRepository.deleteByUserId(userId);

        return savedOrder;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }


}