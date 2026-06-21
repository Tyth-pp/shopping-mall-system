package com.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员表
 */
@Data
public class Admin implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String realName;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
