package com.mall.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItem implements Serializable {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImg;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
