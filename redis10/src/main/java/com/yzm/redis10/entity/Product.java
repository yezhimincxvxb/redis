package com.yzm.redis10.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 商品名
     */
    private String name;

    /**
     * 库存
     */
    private Integer leftNum;


}
