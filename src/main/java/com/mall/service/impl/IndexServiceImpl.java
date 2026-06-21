package com.mall.service.impl;

import com.mall.mapper.BannerMapper;
import com.mall.mapper.ProductCategoryMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.IndexService;
import com.mall.util.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class IndexServiceImpl implements IndexService {

    private static final String CACHE_KEY = "mall:index:data";
    private static final long CACHE_TTL = 30; // 30 分钟

    private final BannerMapper bannerMapper;
    private final ProductMapper productMapper;
    private final ProductCategoryMapper categoryMapper;
    private final RedisUtil redisUtil;

    public IndexServiceImpl(BannerMapper bannerMapper, ProductMapper productMapper,
                            ProductCategoryMapper categoryMapper, RedisUtil redisUtil) {
        this.bannerMapper = bannerMapper;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> indexData() {
        // 先读缓存
        Object cached = redisUtil.get(CACHE_KEY);
        if (cached instanceof Map) {
            return (Map<String, Object>) cached;
        }

        // 缓存未命中，查库
        Map<String, Object> data = new HashMap<>();
        data.put("banners", bannerMapper.selectAllEnabled());
        data.put("hotProducts", productMapper.selectPage(null, null, 1, "sales"));
        data.put("newProducts", productMapper.selectPage(null, null, 1, null));
        data.put("categories", categoryMapper.selectAll());

        // 写入缓存
        redisUtil.set(CACHE_KEY, data, CACHE_TTL, TimeUnit.MINUTES);
        return data;
    }

    /** 清除首页缓存（商品/分类/轮播图变更时调用） */
    public void evictCache() {
        redisUtil.delete(CACHE_KEY);
    }
}
