package com.mall.mapper;

import com.mall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderMapper {
    Order selectById(@Param("id") Long id);
    Order selectByOrderNo(@Param("orderNo") String orderNo);
    List<Order> selectPage(@Param("orderNo") String orderNo, @Param("status") Integer status,
                           @Param("startTime") String startTime, @Param("endTime") String endTime);
    int insert(Order order);
    int updatePrice(@Param("id") Long id, @Param("totalAmount") java.math.BigDecimal totalAmount);
    int updatePayStatus(@Param("id") Long id, @Param("payStatus") Integer payStatus);
    int updateShipStatus(@Param("id") Long id, @Param("shipStatus") Integer shipStatus,
                         @Param("trackingNo") String trackingNo);
}
