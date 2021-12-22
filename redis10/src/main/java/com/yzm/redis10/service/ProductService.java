package com.yzm.redis10.service;

import com.yzm.redis10.entity.Product;

import java.util.List;

public interface ProductService {

    Product getById(Integer id);

    void updateById(Product product);

    List<Product> list();

}
