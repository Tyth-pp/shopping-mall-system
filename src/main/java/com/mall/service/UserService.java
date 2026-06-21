package com.mall.service;

import com.mall.entity.Address;
import com.mall.entity.User;
import com.mall.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface UserService {

    User getById(Long id);
    User getByUsername(String username);
    Map<String, Object> login(String username, String password);
    void register(User user);
    void updateProfile(User user);

    // 后台管理
    PageVO<User> page(int page, int pageSize, String keyword);
    void updateStatus(Long id, Integer status);
    void updateScore(Long id, Integer score);

    // 地址管理
    List<Address> addressList(Long userId);
    Address getAddressById(Long id);
    void addAddress(Address address);
    void updateAddress(Address address);
    void deleteAddress(Long id, Long userId);
}
