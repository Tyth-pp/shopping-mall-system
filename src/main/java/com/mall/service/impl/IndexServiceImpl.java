package com.mall.service.impl;

import com.mall.mapper.BannerMapper;
import com.mall.mapper.ProductCategoryMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.IndexService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IndexServiceImpl implements IndexService {

    private final BannerMapper bannerMapper;
    private final ProductMapper productMapper;
    private final ProductCategoryMapper categoryMapper;

    public IndexServiceImpl(BannerMapper bannerMapper, ProductMapper productMapper,
                            ProductCategoryMapper categoryMapper) {
        this.bannerMapper = bannerMapper;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Map<String, Object> indexData() {
        Map<String, Object> data = new HashMap<>();
        data.put("banners", bannerMapper.selectAllEnabled());
        data.put("hotProducts", productMapper.selectPage(null, null, 1, "sales"));
        data.put("newProducts", productMapper.selectPage(null, null, 1, null));
        data.put("categories", categoryMapper.selectAll());
        return data;
    }
}
