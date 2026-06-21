package com.mall.controller.fore;

import com.mall.service.CartService;
import com.mall.vo.CartVO;
import com.mall.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "前台-购物车")
@RestController
@RequestMapping("/api/fore/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @Operation(summary = "购物车列表")
    @GetMapping("/list")
    public Result<List<CartVO>> list(HttpServletRequest request) {
        return Result.success(cartService.list(getUserId(request)));
    }

    @Operation(summary = "加入购物车")
    @PostMapping("/add")
    public Result<Void> add(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = params.get("quantity") != null
                ? Integer.valueOf(params.get("quantity").toString()) : 1;
        cartService.add(getUserId(request), productId, quantity);
        return Result.success();
    }

    @Operation(summary = "修改数量")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Map<String, Object> params) {
        Long cartId = Long.valueOf(params.get("cartId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        cartService.update(cartId, quantity);
        return Result.success();
    }

    @Operation(summary = "删除购物车项")
    @DeleteMapping("/delete/{cartId}")
    public Result<Void> delete(@PathVariable Long cartId) {
        cartService.delete(cartId);
        return Result.success();
    }

    @Operation(summary = "勾选/取消勾选")
    @PutMapping("/check")
    public Result<Void> check(@RequestBody Map<String, Object> params) {
        Long cartId = Long.valueOf(params.get("cartId").toString());
        Integer checked = Integer.valueOf(params.get("checked").toString());
        cartService.check(cartId, checked);
        return Result.success();
    }

    @Operation(summary = "获取勾选项（结算用）")
    @GetMapping("/checked")
    public Result<List<CartVO>> checked(HttpServletRequest request) {
        return Result.success(cartService.getChecked(getUserId(request)));
    }
}
