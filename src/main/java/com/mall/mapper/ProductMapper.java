package com.mall.mapper;

import com.mall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    Product selectById(@Param("id") Long id);
    List<Product> selectPage(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
                             @Param("status") Integer status, @Param("sort") String sort);
    int insert(Product product);
    int update(Product product);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int deleteById(@Param("id") Long id);
}
