package com.yzm.redis12.service.impl;

import com.yzm.redis12.entity.Product;
import com.yzm.redis12.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Map<Integer, Product> productMap;

    static {
        productMap = new HashMap<>();
        productMap.put(productMap.size() + 1, Product.builder().id(productMap.size() + 1).name("apple").leftNum(10).build());
        productMap.put(productMap.size() + 1, Product.builder().id(productMap.size() + 1).name("banana").leftNum(10).build());
    }

    @Override
    public Product getById(Integer id) {
        return productMap.get(id);
    }

    @Override
    public List<Product> list() {
        return new ArrayList<>(productMap.values());
    }
}
