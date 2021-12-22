package com.yzm.redis10.service.impl;

import com.yzm.redis10.entity.Product;
import com.yzm.redis10.service.ProductService;
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
        productMap.put(productMap.size() + 1, Product.builder().id(productMap.size() + 1).name("苹果").leftNum(10).build());
    }

    @Override
    public Product getById(Integer id) {
        return productMap.get(id);
    }

    @Override
    public void updateById(Product product) {
        Product update = productMap.get(product.getId());
        update.setLeftNum(product.getLeftNum());
        productMap.put(product.getId(), update);
    }

    @Override
    public List<Product> list() {
        return new ArrayList<>(productMap.values());
    }
}
