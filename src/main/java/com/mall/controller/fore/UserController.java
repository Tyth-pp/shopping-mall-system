package com.mall.controller.fore;

import com.mall.entity.Address;
import com.mall.entity.User;
import com.mall.service.UserService;
import com.mall.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "前台-用户")
@RestController
@RequestMapping("/api/fore/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<User> profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getById(userId));
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody User user) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        userService.updateProfile(user);
        return Result.success();
    }

    @Operation(summary = "找回密码")
    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody Map<String, String> params) {
        User user = userService.getByUsername(params.get("username"));
        if (user == null) return Result.fail("用户不存在");
        user.setPassword(com.mall.util.Md5Util.encrypt(params.get("newPassword")));
        userService.updateProfile(user);
        return Result.success();
    }

    // ---------- 收货地址 ----------
    @Operation(summary = "获取收货地址列表")
    @GetMapping("/address")
    public Result<List<Address>> addressList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.addressList(userId));
    }

    @Operation(summary = "新增收货地址")
    @PostMapping("/address")
    public Result<Void> addAddress(HttpServletRequest request, @RequestBody Address address) {
        Long userId = (Long) request.getAttribute("userId");
        address.setUserId(userId);
        userService.addAddress(address);
        return Result.success();
    }

    @Operation(summary = "修改收货地址")
    @PutMapping("/address/{id}")
    public Result<Void> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        address.setId(id);
        userService.updateAddress(address);
        return Result.success();
    }

    @Operation(summary = "删除收货地址")
    @DeleteMapping("/address/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.deleteAddress(id, userId);
        return Result.success();
    }
}
