package com.mall.mapper;

import com.mall.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartMapper {
    List<Cart> selectByUserId(@Param("userId") Long userId);
    List<Cart> selectCheckedByUserId(@Param("userId") Long userId);
    Cart selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    int insert(Cart cart);
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    int updateChecked(@Param("id") Long id, @Param("checked") Integer checked);
    int deleteById(@Param("id") Long id);
    int deleteCheckedByUserId(@Param("userId") Long userId);
}
