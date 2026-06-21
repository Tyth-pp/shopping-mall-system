package com.mall.mapper;

import com.mall.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    java.util.List<Admin> selectAll();
    Admin selectById(@Param("id") Long id);
    Admin selectByUsername(@Param("username") String username);
    int insert(Admin admin);
    int update(Admin admin);
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
