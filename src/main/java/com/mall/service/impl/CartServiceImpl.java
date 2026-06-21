package com.mall.service.impl;

import com.mall.entity.Cart;
import com.mall.entity.Product;
import com.mall.exception.BusinessException;
import com.mall.exception.ErrorCode;
import com.mall.mapper.CartMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.CartService;
import com.mall.vo.CartVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    public CartServiceImpl(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<CartVO> list(Long userId) {
        return toCartVOList(cartMapper.selectByUserId(userId));
    }

    @Override
    @Transactional
    public void add(Long userId, Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        Cart exist = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (exist != null) {
            cartMapper.updateQuantity(exist.getId(), exist.getQuantity() + quantity);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(1);
            cartMapper.insert(cart);
        }
    }

    @Override
    @Transactional
    public void update(Long cartId, Integer quantity) {
        cartMapper.updateQuantity(cartId, quantity);
    }

    @Override
    @Transactional
    public void delete(Long cartId) {
        cartMapper.deleteById(cartId);
    }

    @Override
    @Transactional
    public void check(Long cartId, Integer checked) {
        cartMapper.updateChecked(cartId, checked);
    }

    @Override
    public List<CartVO> getChecked(Long userId) {
        return toCartVOList(cartMapper.selectCheckedByUserId(userId));
    }

    private List<CartVO> toCartVOList(List<Cart> carts) {
        List<CartVO> vos = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setUserId(cart.getUserId());
            vo.setProductId(cart.getProductId());
            vo.setQuantity(cart.getQuantity());
            vo.setChecked(cart.getChecked());
            vo.setCreateTime(cart.getCreateTime());
            vo.setUpdateTime(cart.getUpdateTime());
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setProductImage(product.getMainImage());
                vo.setProductPrice(product.getPrice());
                vo.setProductStock(product.getStock());
                vo.setProductOnShelf(product.getStatus() == 1);
            }
            vos.add(vo);
        }
        return vos;
    }
}
