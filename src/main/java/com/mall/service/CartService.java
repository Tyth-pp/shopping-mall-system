package com.mall.service;

import com.mall.vo.CartVO;

import java.util.List;

public interface CartService {
    List<CartVO> list(Long userId);
    void add(Long userId, Long productId, Integer quantity);
    void update(Long cartId, Integer quantity);
    void delete(Long cartId);
    void check(Long cartId, Integer checked);
    List<CartVO> getChecked(Long userId);
}
