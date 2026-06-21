package com.mall.controller.fore;

import com.mall.entity.Product;
import com.mall.service.ProductService;
import com.mall.util.Result;
import com.mall.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "前台-商品")
@RestController
@RequestMapping("/api/fore/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public Result<PageVO<Product>> list(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "20") Integer pageSize,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String sort) {
        return Result.success(productService.page(page, pageSize, keyword, categoryId, 1));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id, HttpServletRequest request) {
        Product product = productService.getById(id);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("product", product);
        Long userId = (Long) request.getAttribute("userId");
        result.put("collected", userId != null && productService.isCollected(userId, id));
        return Result.success(result);
    }

    @Operation(summary = "收藏商品")
    @PostMapping("/collect")
    public Result<Void> collect(HttpServletRequest request, @RequestBody Map<String, Long> params) {
        Long userId = (Long) request.getAttribute("userId");
        productService.collect(userId, params.get("productId"));
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/collect/{productId}")
    public Result<Void> cancelCollect(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        productService.cancelCollect(userId, productId);
        return Result.success();
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/collect")
    public Result<List<Long>> collectList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(productService.collectList(userId));
    }
}
