package com.mall.service;

import com.mall.entity.Product;
import com.mall.entity.ProductCategory;
import com.mall.vo.PageVO;

import java.util.List;

public interface ProductService {

    PageVO<Product> page(int page, int pageSize, String keyword, Long categoryId, Integer status, String sort);
    Product getById(Long id);
    void add(Product product);
    void update(Product product);
    void updateStatus(Long id, Integer status);
    List<ProductCategory> categoryList();
    List<ProductCategory> categoryTree();
    void addCategory(ProductCategory category);
    void updateCategory(ProductCategory category);

    // 收藏
    void collect(Long userId, Long productId);
    void cancelCollect(Long userId, Long productId);
    List<Long> collectList(Long userId);
    boolean isCollected(Long userId, Long productId);
}
