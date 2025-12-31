package org.monicandy.ecommerceapp.controller;

import org.monicandy.ecommerceapp.service.ProductService;
import org.monicandy.ecommerceapp.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductPageController {

    private final ProductService productService;

    public ProductPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}/page")
    public String productDetailPage(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/products/page")
    public String productListPage(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product-list";
    }

}
