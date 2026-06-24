package com.mall.vo;

import com.mall.entity.Cart;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class  CartVO extends Cart {

    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer productStock;
    private Boolean productOnShelf;
}
