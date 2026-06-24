package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.entity.Address;
import com.mall.entity.Cart;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.Product;
import com.mall.exception.BusinessException;
import com.mall.exception.ErrorCode;
import com.mall.mapper.AddressMapper;
import com.mall.mapper.CartMapper;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.OrderService;
import com.mall.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    private static final int PAY_STATUS_UNPAID = 0;
    private static final int PAY_STATUS_PAID = 1;
    private static final int PAY_STATUS_REFUNDED = 2;
    private static final int PAY_STATUS_CANCELED = 3;
    private static final int SHIP_STATUS_NOT_SHIPPED = 0;
    private static final int SHIP_STATUS_SHIPPED = 1;
    private static final int SHIP_STATUS_RECEIVED = 2;
    private static final int ORDER_PAY_TIMEOUT_MINUTES = 10;

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

    @Override
    @Transactional
    public Map<String, Object> create(Long userId, Long addressId, String remark) {
        List<Cart> checkedItems = cartMapper.selectCheckedByUserId(userId);
        if (checkedItems.isEmpty()) {
            throw new BusinessException("No selected products");
        }

        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("Invalid shipping address");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (Cart cart : checkedItems) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new BusinessException("Product is off shelf");
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT.getCode(),
                        "Insufficient stock for product: " + product.getName());
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

            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales((product.getSales() != null ? product.getSales() : 0) + cart.getQuantity());
            productMapper.update(product);
        }

        Order order = buildUnpaidOrder(userId, addressId, totalAmount, remark);
        orderMapper.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        cartMapper.deleteCheckedByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("totalAmount", totalAmount);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> createBuyNow(Long userId, Long addressId, Long productId, Integer quantity, String remark) {
        int buyQuantity = quantity == null ? 1 : quantity;
        if (buyQuantity < 1) {
            throw new BusinessException("Invalid quantity");
        }

        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("Invalid shipping address");
        }

        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException("Product is off shelf");
        }
        if (product.getStock() < buyQuantity) {
            throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT.getCode(),
                    "Insufficient stock, remaining: " + product.getStock());
        }

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(buyQuantity));
        product.setStock(product.getStock() - buyQuantity);
        product.setSales((product.getSales() != null ? product.getSales() : 0) + buyQuantity);
        productMapper.update(product);

        Order order = buildUnpaidOrder(userId, addressId, totalAmount, remark);
        orderMapper.insert(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductImg(product.getMainImage());
        item.setQuantity(buyQuantity);
        item.setUnitPrice(product.getPrice());
        item.setTotalPrice(totalAmount);
        orderItemMapper.insert(item);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
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
        if (isPaymentExpired(order)) {
            cancelUnpaidOrder(order);
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR.getCode(),
                    "Order has exceeded the 10-minute payment limit and was canceled");
        }
        if (order.getPayStatus() != PAY_STATUS_UNPAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        if (orderMapper.payUnpaid(orderId) == 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getPayStatus() != PAY_STATUS_UNPAID || order.getShipStatus() != SHIP_STATUS_NOT_SHIPPED) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }
        cancelUnpaidOrder(order);
    }

    @Transactional
    public int cancelExpiredUnpaidOrders() {
        List<Order> expiredOrders = orderMapper.selectUnpaidBefore(LocalDateTime.now().minusMinutes(ORDER_PAY_TIMEOUT_MINUTES));
        int count = 0;
        for (Order order : expiredOrders) {
            if (cancelUnpaidOrder(order)) {
                count++;
            }
        }
        return count;
    }

    @Override
    @Transactional
    public void confirm(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getShipStatus() != SHIP_STATUS_SHIPPED) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        orderMapper.updateShipStatus(orderId, SHIP_STATUS_RECEIVED, null);
    }

    @Override
    public PageVO<Order> userPage(Long userId, int page, int pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        List<Order> list = orderMapper.selectByUserId(userId, status);
        PageInfo<Order> info = new PageInfo<>(list);
        return PageVO.of(list, info.getTotal(), page, pageSize);
    }

    @Override
    public List<OrderItem> getItems(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

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
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        return order;
    }

    @Override
    @Transactional
    public void updatePrice(Long id, BigDecimal totalAmount) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        orderMapper.updatePrice(id, totalAmount);
    }

    @Override
    @Transactional
    public void ship(Long id, String trackingNo) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getPayStatus() != PAY_STATUS_PAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        orderMapper.updateShipStatus(id, SHIP_STATUS_SHIPPED, trackingNo);
    }

    @Override
    @Transactional
    public void handleRefund(Long id, boolean approve) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (approve) {
            orderMapper.updatePayStatus(id, PAY_STATUS_REFUNDED);
        }
    }

    private Order buildUnpaidOrder(Long userId, Long addressId, BigDecimal totalAmount, String remark) {
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + String.format("%04d", new Random().nextInt(10000));

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayStatus(PAY_STATUS_UNPAID);
        order.setShipStatus(SHIP_STATUS_NOT_SHIPPED);
        order.setAddressId(addressId);
        order.setRemark(remark);
        return order;
    }

    private boolean isPaymentExpired(Order order) {
        return order.getPayStatus() == PAY_STATUS_UNPAID
                && order.getCreateTime() != null
                && !order.getCreateTime().plusMinutes(ORDER_PAY_TIMEOUT_MINUTES).isAfter(LocalDateTime.now());
    }

    private boolean cancelUnpaidOrder(Order order) {
        if (order.getPayStatus() != PAY_STATUS_UNPAID) {
            return false;
        }
        int updated = orderMapper.cancelUnpaid(order.getId());
        if (updated == 0) {
            return false;
        }

        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.update(product);
            }
        }
        return true;
    }
}
