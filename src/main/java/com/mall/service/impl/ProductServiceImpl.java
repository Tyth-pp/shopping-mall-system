package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.entity.Product;
import com.mall.entity.ProductCategory;
import com.mall.exception.BusinessException;
import com.mall.exception.ErrorCode;
import com.mall.mapper.ProductCategoryMapper;
import com.mall.mapper.ProductCollectMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.ProductService;
import com.mall.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductCategoryMapper categoryMapper;
    private final ProductCollectMapper collectMapper;
    private final IndexServiceImpl indexService;

    public ProductServiceImpl(ProductMapper productMapper, ProductCategoryMapper categoryMapper,
                              ProductCollectMapper collectMapper, IndexServiceImpl indexService) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.collectMapper = collectMapper;
        this.indexService = indexService;
    }

    @Override
    public PageVO<Product> page(int page, int pageSize, String keyword, Long categoryId, Integer status) {
        PageHelper.startPage(page, pageSize);
        List<Product> list = productMapper.selectPage(keyword, categoryId, status, null);
        PageInfo<Product> info = new PageInfo<>(list);
        return PageVO.of(list, info.getTotal(), page, pageSize);
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        return product;
    }

    @Override
    @Transactional
    public void add(Product product) {
        product.setStatus(1);
        productMapper.insert(product);
        indexService.evictCache();
    }

    @Override
    @Transactional
    public void update(Product product) {
        if (productMapper.selectById(product.getId()) == null)
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        productMapper.update(product);
        indexService.evictCache();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        if (productMapper.selectById(id) == null)
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        productMapper.updateStatus(id, status);
        indexService.evictCache();
    }

    @Override
    public List<ProductCategory> categoryList() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<ProductCategory> categoryTree() {
        return categoryMapper.selectAll();
    }

    @Override
    @Transactional
    public void addCategory(ProductCategory category) {
        categoryMapper.insert(category);
        indexService.evictCache();
    }

    @Override
    @Transactional
    public void updateCategory(ProductCategory category) {
        categoryMapper.update(category);
        indexService.evictCache();
    }

    // 收藏
    @Override
    @Transactional
    public void collect(Long userId, Long productId) {
        if (productMapper.selectById(productId) == null)
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        if (collectMapper.exists(userId, productId) == 0) {
            collectMapper.insert(userId, productId);
        }
    }

    @Override
    @Transactional
    public void cancelCollect(Long userId, Long productId) {
        collectMapper.delete(userId, productId);
    }

    @Override
    public List<Long> collectList(Long userId) {
        return collectMapper.selectByUserId(userId).stream()
                .map(c -> c.getProductId()).toList();
    }

    @Override
    public boolean isCollected(Long userId, Long productId) {
        return collectMapper.exists(userId, productId) > 0;
    }
}
