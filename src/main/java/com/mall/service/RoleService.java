package com.mall.service;

import com.mall.entity.Permission;
import com.mall.entity.Role;
import java.util.List;

public interface RoleService {
    List<Role> list();
    Role getById(Long id);
    void add(Role role);
    void update(Role role);
    void delete(Long id);
    List<Permission> permissionTree();
    List<Permission> getPermissionsByRoleId(Long roleId);
    void assignPermissions(Long roleId, List<Long> permissionIds);
    List<Role> getRolesByAdminId(Long adminId);
}
