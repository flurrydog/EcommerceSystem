package org.monicandy.ecommerceapp.controller;

import org.monicandy.ecommerceapp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/checkout")
    public String checkoutPage() {
        return "order_checkout";
    }

    @PostMapping("/create")
    public String createOrder(Model model) {
        Long userId = 1L; // 暂时写死

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
        var order = orderService.getOrderById(id);
        var orderItems = orderService.getOrderItemsByOrderId(id);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        return "order_detail";
    }

    /**
     * 订单列表页面
     */
    @GetMapping("/list")
    public String orderList(Model model) {
        Long userId = 1L; // 暂时写死

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

}