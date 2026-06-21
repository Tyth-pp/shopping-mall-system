package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.entity.Address;
import com.mall.entity.User;
import com.mall.exception.BusinessException;
import com.mall.exception.ErrorCode;
import com.mall.mapper.AddressMapper;
import com.mall.mapper.UserMapper;
import com.mall.service.UserService;
import com.mall.util.JwtUtil;
import com.mall.util.Md5Util;
import com.mall.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AddressMapper addressMapper;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, AddressMapper addressMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        if (user.getStatus() == 0) throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
        if (!Md5Util.verify(password, user.getPassword()))
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("avatar", user.getAvatar());
        return result;
    }

    @Override
    @Transactional
    public void register(User user) {
        User exist = userMapper.selectByUsername(user.getUsername());
        if (exist != null) throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        user.setPassword(Md5Util.encrypt(user.getPassword()));
        user.setStatus(1);
        user.setScore(0);
        userMapper.insert(user);
    }

    @Override
    @Transactional
    public void updateProfile(User user) {
        userMapper.update(user);
    }

    // 后台管理
    @Override
    public PageVO<User> page(int page, int pageSize, String keyword) {
        PageHelper.startPage(page, pageSize);
        List<User> list = userMapper.selectPage(keyword);
        PageInfo<User> info = new PageInfo<>(list);
        return PageVO.of(list, info.getTotal(), page, pageSize);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        if (userMapper.selectById(id) == null)
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        userMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void updateScore(Long id, Integer score) {
        if (userMapper.selectById(id) == null)
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        userMapper.updateScore(id, score);
    }

    // 地址管理
    @Override
    public List<Address> addressList(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    public Address getAddressById(Long id) {
        return addressMapper.selectById(id);
    }

    @Override
    @Transactional
    public void addAddress(Address address) {
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(address.getUserId());
        }
        addressMapper.insert(address);
    }

    @Override
    @Transactional
    public void updateAddress(Address address) {
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(address.getUserId());
        }
        addressMapper.update(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id, Long userId) {
        addressMapper.deleteById(id);
    }
}
