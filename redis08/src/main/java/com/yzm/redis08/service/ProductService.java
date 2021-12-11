package com.yzm.redis08.service;

import com.yzm.redis08.entity.Product;

import java.util.List;

public interface ProductService {

    Product getById(Integer id);

    void updateById(Product product);

    List<Product> list();

}
