package com.mall.mapper;

import com.mall.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMapper {
    List<Role> selectAll();
    Role selectById(@Param("id") Long id);
    int insert(Role role);
    int update(Role role);
    int deleteById(@Param("id") Long id);
    List<Role> selectByAdminId(@Param("adminId") Long adminId);
}
