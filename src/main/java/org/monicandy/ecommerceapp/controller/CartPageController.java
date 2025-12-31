package org.monicandy.ecommerceapp.controller;

import org.monicandy.ecommerceapp.service.CartService;
import org.monicandy.ecommerceapp.service.ProductService;
import org.monicandy.ecommerceapp.view.CartPageView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class CartPageController {

    private final CartService cartService;
    private final ProductService productService;

    public CartPageController(CartService cartService,
                              ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId) {
        cartService.addToCart(productId);
        return "redirect:/cart/page";
    }

    @GetMapping("/page")
    public String cartPage(Model model) {
        CartPageView page = cartService.getCartPageView();

        model.addAttribute("items", page.items());
        model.addAttribute("total", page.total());

        return "cart";
    }

}

