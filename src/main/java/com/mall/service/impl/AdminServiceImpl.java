package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.entity.Admin;
import com.mall.exception.BusinessException;
import com.mall.exception.ErrorCode;
import com.mall.mapper.AdminMapper;
import com.mall.mapper.RoleMapper;
import com.mall.service.AdminService;
import com.mall.util.JwtUtil;
import com.mall.util.Md5Util;
import com.mall.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final RoleMapper roleMapper;
    private final JwtUtil jwtUtil;

    public AdminServiceImpl(AdminMapper adminMapper, RoleMapper roleMapper, JwtUtil jwtUtil) {
        this.adminMapper = adminMapper;
        this.roleMapper = roleMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Admin getById(Long id) {
        return adminMapper.selectById(id);
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (admin.getStatus() == 0) {
            throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
        }
        if (!Md5Util.verify(password, admin.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("adminId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("realName", admin.getRealName());
        result.put("roles", roleMapper.selectByAdminId(admin.getId()));
        return result;
    }

    @Override
    @Transactional
    public void updatePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (!Md5Util.verify(oldPassword, admin.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        adminMapper.updatePassword(adminId, Md5Util.encrypt(newPassword));
    }

    @Override
    public PageVO<Admin> list(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        // AdminMapper.selectAll to be added
        return PageVO.of(null, 0, page, pageSize);
    }

    @Override
    @Transactional
    public void add(Admin admin) {
        Admin exist = adminMapper.selectByUsername(admin.getUsername());
        if (exist != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        admin.setPassword(Md5Util.encrypt(admin.getPassword()));
        admin.setStatus(1);
        adminMapper.insert(admin);
    }

    @Override
    @Transactional
    public void assignRole(Long adminId, Long roleId) {
        // TODO: Insert into admin_role table
    }
}
