package com.mall.service;

import com.mall.entity.ProductComment;
import com.mall.vo.PageVO;

public interface CommentService {
    PageVO<ProductComment> pageByProductId(Long productId, int page, int pageSize);
    void add(ProductComment comment);
    void append(Long commentId, String appendContent);
}
