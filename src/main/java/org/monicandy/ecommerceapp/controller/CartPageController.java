package org.monicandy.ecommerceapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.monicandy.ecommerceapp.service.CartService;
import org.monicandy.ecommerceapp.service.ProductService;
import org.monicandy.ecommerceapp.service.UserService;
import org.monicandy.ecommerceapp.entity.User;
import org.monicandy.ecommerceapp.view.CartPageView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/cart")
public class CartPageController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;

    public CartPageController(CartService cartService,
                              ProductService productService,
                              UserService userService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {  // 添加HttpServletRequest参数

        // 1. 检查用户是否登录
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                "anonymousUser".equals(auth.getPrincipal())) {

            // 保存要添加的商品ID，登录后自动加入购物车
            HttpSession session = request.getSession();
            session.setAttribute("productToAddAfterLogin", productId);

            redirectAttributes.addFlashAttribute("errorMessage", "请先登录！");
            return "redirect:/login";
        }

        // 2. 获取当前用户ID
        Long userId = getCurrentUserId(auth);  // 修改为接收auth参数

        // 3. 添加商品到购物车
        try {
            cartService.addToCart(userId, productId);
            redirectAttributes.addFlashAttribute("successMessage", "商品已成功加入购物车！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "加入购物车失败：" + e.getMessage());
        }

        // 4. 跳转到购物车页面
        return "redirect:/cart/page";
    }

    @GetMapping("/page")
    public String cartPage(Model model, RedirectAttributes redirectAttributes) {
        // 1. 获取认证信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 2. 检查是否登录
        if (auth == null || !auth.isAuthenticated() ||
                "anonymousUser".equals(auth.getPrincipal())) {
            redirectAttributes.addFlashAttribute("errorMessage", "请先登录查看购物车！");
            return "redirect:/login";
        }

        // 3. 获取当前用户ID
        Long userId = getCurrentUserId(auth);

        // 4. 获取购物车数据
        try {
            CartPageView page = cartService.getCartPageView(userId);
            model.addAttribute("items", page.items());
            model.addAttribute("total", page.total());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "加载购物车失败：" + e.getMessage());
        }

        return "cart";
    }

    /**
     * 获取当前登录用户的ID
     */
    private Long getCurrentUserId(Authentication authentication) {
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

