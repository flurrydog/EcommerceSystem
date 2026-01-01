package org.monicandy.ecommerceapp.controller;

import org.monicandy.ecommerceapp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.monicandy.ecommerceapp.service.UserService;
import org.monicandy.ecommerceapp.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService; // 注入UserService

    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        // 获取当前登录用户
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        // 获取用户ID
        Long userId = getCurrentUserId();
        model.addAttribute("userId", userId);

        return "order_checkout";
    }

    @PostMapping("/create")
    public String createOrder(Model model) {
        // 使用当前登录用户ID
        Long userId = getCurrentUserId();

        try {
            var order = orderService.createOrderFromCart(userId);
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart/page";
        }
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        // 检查订单是否属于当前用户
        Long userId = getCurrentUserId();
        var order = orderService.getOrderById(id);

        // 权限验证：只能查看自己的订单
        if (order == null || !order.getUserId().equals(userId)) {
            model.addAttribute("error", "订单不存在或无权限查看");
            return "redirect:/orders/list";
        }

        var orderItems = orderService.getOrderItemsByOrderId(id);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        return "order_detail";
    }

    /**
     * 订单列表页面 - 只显示当前用户的订单
     */
    @GetMapping("/list")
    public String orderList(Model model) {
        // 使用当前登录用户ID
        Long userId = getCurrentUserId();

        // 只获取当前用户的订单
        var orders = orderService.getOrdersByUserId(userId);

        // 为每个订单添加商品数量信息
        List<Map<String, Object>> ordersWithCount = orders.stream().map(order -> {
            var orderItems = orderService.getOrderItemsByOrderId(order.getId());
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("userId", order.getUserId());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("status", order.getStatus());
            orderMap.put("createTime", order.getCreateTime());
            orderMap.put("itemCount", orderItems.size());
            return orderMap;
        }).toList();

        model.addAttribute("orders", ordersWithCount);
        return "order_list";
    }

    /**
     * 获取当前登录用户的ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("用户未登录");
        }

        // 获取用户名
        String username;
        if (authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            username = authentication.getName();
        }

        // 通过用户名获取用户ID
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + username));

        return user.getId();
    }

}