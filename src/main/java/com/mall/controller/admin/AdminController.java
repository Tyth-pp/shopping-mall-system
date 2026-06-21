package com.mall.controller.admin;

import com.mall.service.AdminService;
import com.mall.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "后台-管理员")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "管理员个人信息")
    @GetMapping("/profile")
    public Result<?> profile(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        return Result.success(adminService.getById(adminId));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(HttpServletRequest request, @RequestBody Map<String, String> params) {
        Long adminId = (Long) request.getAttribute("userId");
        adminService.updatePassword(adminId, params.get("oldPassword"), params.get("newPassword"));
        return Result.success();
    }
}
