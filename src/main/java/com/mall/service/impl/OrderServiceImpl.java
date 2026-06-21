package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.entity.*;
import com.mall.exception.BusinessException;
import com.mall.exception.ErrorCode;
import com.mall.mapper.*;
import com.mall.service.OrderService;
import com.mall.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                            CartMapper cartMapper, ProductMapper productMapper,
                            AddressMapper addressMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.addressMapper = addressMapper;
    }

    // ==================== 前台用户 ====================

    @Override
    @Transactional
    public Map<String, Object> create(Long userId, Long addressId, String remark) {
        // 1. 获取勾选的购物车项
        List<Cart> checkedItems = cartMapper.selectCheckedByUserId(userId);
        if (checkedItems.isEmpty()) {
            throw new BusinessException("没有勾选的商品");
        }

        // 2. 校验地址
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址无效");
        }

        // 3. 计算总金额 + 校验库存 + 构建订单项
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (Cart cart : checkedItems) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new BusinessException("商品【" + (product != null ? product.getName() : "未知") + "】已下架");
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT.getCode(),
                        "商品【" + product.getName() + "】库存不足，剩余" + product.getStock());
            }

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImg(product.getMainImage());
            item.setQuantity(cart.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            items.add(item);
            totalAmount = totalAmount.add(item.getTotalPrice());

            // 扣库存
            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales((product.getSales() != null ? product.getSales() : 0) + cart.getQuantity());
            productMapper.update(product);
        }

        // 4. 生成订单
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + String.format("%04d", new Random().nextInt(10000));

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayStatus(0);
        order.setShipStatus(0);
        order.setAddressId(addressId);
        order.setRemark(remark);
        orderMapper.insert(order);

        // 5. 保存订单项
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 6. 清空购物车勾选项
        cartMapper.deleteCheckedByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", orderNo);
        result.put("totalAmount", totalAmount);
        return result;
    }

    @Override
    @Transactional
    public void pay(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getPayStatus() != 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        orderMapper.updatePayStatus(orderId, 1);
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getPayStatus() != 0 && order.getShipStatus() != 0) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }
        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.update(product);
            }
        }
        orderMapper.updatePayStatus(orderId, 2);
    }

    @Override
    @Transactional
    public void confirm(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getShipStatus() != 1) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        orderMapper.updateShipStatus(orderId, 2, null);
    }

    @Override
    public PageVO<Order> userPage(Long userId, int page, int pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        // Use existing selectPage with user filter — or we add a userId param
        List<Order> all = orderMapper.selectPage(null, status, null, null);
        List<Order> filtered = all.stream()
                .filter(o -> o.getUserId().equals(userId))
                .toList();
        return PageVO.of(filtered, filtered.size(), page, pageSize);
    }

    @Override
    public List<OrderItem> getItems(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    // ==================== 后台管理 ====================

    @Override
    public PageVO<Order> page(int page, int pageSize, String orderNo, Integer status,
                               String startTime, String endTime) {
        PageHelper.startPage(page, pageSize);
        List<Order> list = orderMapper.selectPage(orderNo, status, startTime, endTime);
        PageInfo<Order> info = new PageInfo<>(list);
        return PageVO.of(list, info.getTotal(), page, pageSize);
    }

    @Override
    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        return order;
    }

    @Override
    @Transactional
    public void updatePrice(Long id, BigDecimal totalAmount) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        orderMapper.updatePrice(id, totalAmount);
    }

    @Override
    @Transactional
    public void ship(Long id, String trackingNo) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        if (order.getPayStatus() != 1) throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        orderMapper.updateShipStatus(id, 1, trackingNo);
    }

    @Override
    @Transactional
    public void handleRefund(Long id, boolean approve) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        if (approve) {
            orderMapper.updatePayStatus(id, 2);
        }
    }
}
