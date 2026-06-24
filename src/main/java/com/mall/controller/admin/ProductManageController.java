package com.mall.controller.admin;

import com.mall.annotation.Log;
import com.mall.entity.Product;
import com.mall.entity.ProductCategory;
import com.mall.service.ProductService;
import com.mall.util.Result;
import com.mall.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "后台-商品管理")
@RestController
@RequestMapping("/api/admin/product")
public class ProductManageController {

    private final ProductService productService;

    public ProductManageController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public Result<PageVO<Product>> list(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "20") Integer pageSize,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) Integer status) {
        return Result.success(productService.page(page, pageSize, keyword, categoryId, status, null));
    }

    @Operation(summary = "新增商品")
    @PostMapping("/add")
    @Log(value = "新增商品", type = "INSERT")
    @RequiresPermissions("product:add")
    public Result<Void> add(@RequestBody Product product) {
        productService.add(product);
        return Result.success();
    }

    @Operation(summary = "修改商品")
    @PutMapping("/update")
    @Log(value = "修改商品", type = "UPDATE")
    @RequiresPermissions("product:update")
    public Result<Void> update(@RequestBody Product product) {
        productService.update(product);
        return Result.success();
    }

    @Operation(summary = "商品上架/下架")
    @PutMapping("/status/{id}")
    @Log(value = "修改商品状态", type = "UPDATE")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        productService.updateStatus(id, params.get("status"));
        return Result.success();
    }

    @Operation(summary = "分类列表")
    @GetMapping("/category/list")
    public Result<List<ProductCategory>> categoryList() {
        return Result.success(productService.categoryList());
    }

    @Operation(summary = "新增分类")
    @PostMapping("/category/add")
    @Log(value = "新增分类", type = "INSERT")
    public Result<Void> addCategory(@RequestBody ProductCategory category) {
        productService.addCategory(category);
        return Result.success();
    }

    @Operation(summary = "修改分类")
    @PutMapping("/category/update")
    @Log(value = "修改分类", type = "UPDATE")
    public Result<Void> updateCategory(@RequestBody ProductCategory category) {
        productService.updateCategory(category);
        return Result.success();
    }
}
