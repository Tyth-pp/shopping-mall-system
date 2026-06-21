package com.mall.controller.fore;

import com.mall.annotation.RepeatSubmit;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.service.OrderService;
import com.mall.util.Result;
import com.mall.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "前台-订单")
@RestController
@RequestMapping("/api/fore/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @Operation(summary = "生成订单")
    @PostMapping("/create")
    @RepeatSubmit(expire = 5000)
    public Result<Map<String, Object>> create(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long addressId = Long.valueOf(params.get("addressId").toString());
        String remark = (String) params.getOrDefault("remark", null);
        return Result.success(orderService.create(getUserId(request), addressId, remark));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay")
    public Result<Void> pay(HttpServletRequest request, @RequestBody Map<String, Long> params) {
        orderService.pay(params.get("orderId"), getUserId(request));
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancel(@PathVariable Long orderId, HttpServletRequest request) {
        orderService.cancel(orderId, getUserId(request));
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PutMapping("/confirm/{orderId}")
    public Result<Void> confirm(@PathVariable Long orderId, HttpServletRequest request) {
        orderService.confirm(orderId, getUserId(request));
        return Result.success();
    }

    @Operation(summary = "订单列表")
    @GetMapping("/list")
    public Result<PageVO<Order>> list(HttpServletRequest request,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) Integer status) {
        return Result.success(orderService.userPage(getUserId(request), page, pageSize, status));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/detail/{orderId}")
    public Result<Map<String, Object>> detail(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        List<OrderItem> items = orderService.getItems(orderId);
        return Result.success(Map.of("order", order, "items", items));
    }

    @Operation(summary = "申请退货退款")
    @PostMapping("/refund")
    public Result<Void> refund(HttpServletRequest request, @RequestBody Map<String, Long> params) {
        orderService.handleRefund(params.get("orderId"), true);
        return Result.success();
    }
}
