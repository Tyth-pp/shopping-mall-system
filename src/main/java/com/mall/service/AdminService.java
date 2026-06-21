package com.mall.service;

import com.mall.entity.Admin;
import com.mall.vo.PageVO;
import java.util.Map;

public interface AdminService {
    Admin getById(Long id);
    Map<String, Object> login(String username, String password);
    void updatePassword(Long adminId, String oldPassword, String newPassword);
    PageVO<Admin> list(int page, int pageSize);
    void add(Admin admin);
    void assignRole(Long adminId, Long roleId);
}
