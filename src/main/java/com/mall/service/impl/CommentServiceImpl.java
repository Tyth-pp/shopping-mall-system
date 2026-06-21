package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.entity.ProductComment;
import com.mall.exception.BusinessException;
import com.mall.mapper.ProductCommentMapper;
import com.mall.service.CommentService;
import com.mall.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final ProductCommentMapper commentMapper;

    public CommentServiceImpl(ProductCommentMapper commentMapper) {
        this.commentMapper = commentMapper;
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
        commentMapper.insert(comment);
    }

    @Override
    @Transactional
    public void append(Long commentId, String appendContent) {
        commentMapper.updateAppend(commentId, appendContent);
    }
}
