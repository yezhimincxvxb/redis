package com.yzm.lock.service.impl;

import com.yzm.lock.entity.Product;
import com.yzm.lock.mapper.ProductMapper;
import com.yzm.lock.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品 服务实现类
 * </p>
 *
 * @author Yzm
 * @since 2021-05-29
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
