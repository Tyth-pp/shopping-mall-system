package com.mall.service;

import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.vo.PageVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService {

    // 后台管理
    PageVO<Order> page(int page, int pageSize, String orderNo, Integer status, String startTime, String endTime);
    Order getById(Long id);
    void updatePrice(Long id, BigDecimal totalAmount);
    void ship(Long id, String trackingNo);
    void handleRefund(Long id, boolean approve);

    // 前台用户
    Map<String, Object> create(Long userId, Long addressId, String remark);
    void pay(Long orderId, Long userId);
    void cancel(Long orderId, Long userId);
    void confirm(Long orderId, Long userId);
    PageVO<Order> userPage(Long userId, int page, int pageSize, Integer status);
    List<OrderItem> getItems(Long orderId);
}
