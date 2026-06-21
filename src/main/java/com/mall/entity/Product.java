package com.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品主表
 */
@Data
public class Product implements Serializable {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer status;         // 1=上架 0=下架
    private Long categoryId;
    private String mainImage;
    private Integer sales;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
