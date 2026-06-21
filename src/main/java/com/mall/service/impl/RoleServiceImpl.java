package com.mall.service.impl;

import com.mall.entity.Permission;
import com.mall.entity.Role;
import com.mall.mapper.PermissionMapper;
import com.mall.mapper.RoleMapper;
import com.mall.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public RoleServiceImpl(RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<Role> list() {
        return roleMapper.selectAll();
    }

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(Role role) {
        roleMapper.insert(role);
    }

    @Override
    @Transactional
    public void update(Role role) {
        roleMapper.update(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        permissionMapper.deleteRolePermissions(id);
        roleMapper.deleteById(id);
    }

    @Override
    public List<Permission> permissionTree() {
        return permissionMapper.selectAll();
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        permissionMapper.deleteRolePermissions(roleId);
        for (Long permId : permissionIds) {
            permissionMapper.insertRolePermission(roleId, permId);
        }
    }

    @Override
    public List<Role> getRolesByAdminId(Long adminId) {
        return roleMapper.selectByAdminId(adminId);
    }
}
