package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.ProductComment;
import com.mall.exception.BusinessException;
import com.mall.exception.ErrorCode;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductCommentMapper;
import com.mall.service.CommentService;
import com.mall.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final ProductCommentMapper commentMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public CommentServiceImpl(ProductCommentMapper commentMapper, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.commentMapper = commentMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public PageVO<ProductComment> pageByProductId(Long productId, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<ProductComment> list = commentMapper.selectByProductId(productId);
        PageInfo<ProductComment> info = new PageInfo<>(list);
        return PageVO.of(list, info.getTotal(), page, pageSize);
    }

    @Override
    @Transactional
    public void add(ProductComment comment) {
        if (comment.getRating() == null || comment.getRating() < 1 || comment.getRating() > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        if (comment.getOrderId() == null || comment.getProductId() == null) {
            throw new BusinessException("评价订单信息不完整");
        }
        Order order = orderMapper.selectById(comment.getOrderId());
        if (order == null || !order.getUserId().equals(comment.getUserId())) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getShipStatus() == null || order.getShipStatus() != 2) {
            throw new BusinessException("确认收货后才能评价");
        }
        boolean productInOrder = false;
        for (OrderItem item : orderItemMapper.selectByOrderId(comment.getOrderId())) {
            if (item.getProductId().equals(comment.getProductId())) {
                productInOrder = true;
                break;
            }
        }
        if (!productInOrder) {
            throw new BusinessException("该商品不属于当前订单");
        }
        if (commentMapper.selectByOrderAndProduct(comment.getOrderId(), comment.getProductId()) != null) {
            throw new BusinessException("该商品已评价");
        }
        commentMapper.insert(comment);
    }

    @Override
    @Transactional
    public void append(Long userId, Long commentId, String appendContent) {
        ProductComment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new BusinessException("评价不存在");
        }
        if (comment.getAppendContent() != null && !comment.getAppendContent().isBlank()) {
            throw new BusinessException("已追评，不能重复追评");
        }
        if (appendContent == null || appendContent.isBlank()) {
            throw new BusinessException("追评内容不能为空");
        }
        commentMapper.updateAppend(commentId, appendContent);
    }
}
