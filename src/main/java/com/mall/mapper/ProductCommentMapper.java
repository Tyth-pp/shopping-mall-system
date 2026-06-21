package com.mall.mapper;

import com.mall.entity.ProductComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductCommentMapper {
    List<ProductComment> selectByProductId(@Param("productId") Long productId);
    int insert(ProductComment comment);
    int updateAppend(@Param("id") Long id, @Param("appendContent") String appendContent);
}
