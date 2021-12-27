package com.yzm.redis12.service;

import com.yzm.redis12.entity.Product;

import java.util.List;

public interface ProductService {

    Product getById(Integer id);

    List<Product> list();

}
