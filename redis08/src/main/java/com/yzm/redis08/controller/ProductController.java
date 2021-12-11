package com.yzm.redis08.controller;


import com.yzm.redis08.entity.Product;
import com.yzm.redis08.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    public List<Product> list() {
        return productService.list();
    }

}
