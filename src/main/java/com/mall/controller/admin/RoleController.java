package com.mall.controller.admin;

import com.mall.annotation.Log;
import com.mall.entity.Permission;
import com.mall.entity.Role;
import com.mall.service.RoleService;
import com.mall.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "后台-角色权限")
@RestController
@RequestMapping("/api/admin")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // ---------- 角色 ----------
    @Operation(summary = "角色列表")
    @GetMapping("/role/list")
    public Result<List<Role>> roleList() {
        return Result.success(roleService.list());
    }

    @Operation(summary = "新增角色")
    @PostMapping("/role/add")
    @Log(value = "新增角色", type = "INSERT")
    public Result<Void> addRole(@RequestBody Role role) {
        roleService.add(role);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @PutMapping("/role/update")
    @Log(value = "修改角色", type = "UPDATE")
    public Result<Void> updateRole(@RequestBody Role role) {
        roleService.update(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/role/{id}")
    @Log(value = "删除角色", type = "DELETE")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    // ---------- 权限 ----------
    @Operation(summary = "权限树")
    @GetMapping("/permission/tree")
    public Result<List<Permission>> permissionTree() {
        return Result.success(roleService.permissionTree());
    }

    @Operation(summary = "获取角色已有权限")
    @GetMapping("/role/{roleId}/permissions")
    public Result<List<Permission>> rolePermissions(@PathVariable Long roleId) {
        return Result.success(roleService.getPermissionsByRoleId(roleId));
    }

    @Operation(summary = "给角色分配权限")
    @PostMapping("/role/permission")
    @Log(value = "分配角色权限", type = "UPDATE")
    public Result<Void> assignPermission(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) params.get("permissionIds");
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }

    // ---------- 管理员账号 ----------
    @Operation(summary = "管理员列表")
    @GetMapping("/admin/list")
    public Result<?> adminList() {
        // Use AdminService if needed
        return Result.success();
    }

    @Operation(summary = "新增管理员")
    @PostMapping("/admin/add")
    @Log(value = "新增管理员", type = "INSERT")
    public Result<Void> addAdmin(@RequestBody Map<String, Object> params) {
        // TODO: Inject AdminService for admin account operations
        return Result.success();
    }

    @Operation(summary = "给管理员分配角色")
    @PutMapping("/admin/role")
    @Log(value = "分配管理员角色", type = "UPDATE")
    public Result<Void> assignAdminRole(@RequestBody Map<String, Long> params) {
        // TODO: Inject AdminService for role assignment
        return Result.success();
    }
}
