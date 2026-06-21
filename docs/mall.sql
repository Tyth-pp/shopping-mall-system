-- ============================================================
-- 购物商城系统 数据库建表 + 初始化数据
-- MySQL 8.0+ / charset utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS mall
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE mall;

-- ============================================================
-- 1. 买家用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`      VARCHAR(50)     NOT NULL                 COMMENT '用户名',
    `password`      VARCHAR(64)     NOT NULL                 COMMENT '密码（MD5加盐）',
    `email`         VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    `phone`         VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    `avatar`        VARCHAR(255)    DEFAULT NULL             COMMENT '头像URL',
    `score`         INT             NOT NULL DEFAULT 0       COMMENT '积分',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态：1=正常 0=禁用',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='买家用户表';

-- ============================================================
-- 2. 管理员表
-- ============================================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '管理员ID',
    `username`        VARCHAR(50)   NOT NULL                 COMMENT '用户名',
    `password`        VARCHAR(64)   NOT NULL                 COMMENT '密码（MD5加盐）',
    `real_name`       VARCHAR(50)   DEFAULT NULL             COMMENT '真实姓名',
    `status`          TINYINT       NOT NULL DEFAULT 1       COMMENT '状态：1=正常 0=禁用',
    `last_login_time` DATETIME      DEFAULT NULL             COMMENT '最后登录时间',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ============================================================
-- 3. 角色表
-- ============================================================
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '角色ID',
    `name`        VARCHAR(50)   NOT NULL                 COMMENT '角色名称',
    `code`        VARCHAR(50)   NOT NULL                 COMMENT '角色编码（shiro权限标识）',
    `description` VARCHAR(255)  DEFAULT NULL             COMMENT '角色描述',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================================
-- 4. 权限表
-- ============================================================
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '权限ID',
    `name`        VARCHAR(50)   NOT NULL                 COMMENT '权限名称',
    `code`        VARCHAR(100)  NOT NULL                 COMMENT '权限编码（如 product:add）',
    `url`         VARCHAR(255)  DEFAULT NULL             COMMENT '接口路径',
    `parent_id`   BIGINT        NOT NULL DEFAULT 0       COMMENT '父权限ID（0=顶级菜单）',
    `type`        TINYINT       NOT NULL DEFAULT 1       COMMENT '类型：1=菜单 2=按钮 3=接口',
    `sort_order`  INT           DEFAULT 0                COMMENT '排序号',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ============================================================
-- 5. 管理员-角色中间表
-- ============================================================
DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role` (
    `id`       BIGINT NOT NULL AUTO_INCREMENT,
    `admin_id` BIGINT NOT NULL COMMENT '管理员ID',
    `role_id`  BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员-角色中间表';

-- ============================================================
-- 6. 角色-权限中间表
-- ============================================================
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
    `id`            BIGINT NOT NULL AUTO_INCREMENT,
    `role_id`       BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限中间表';

-- ============================================================
-- 7. 商品分类表（一级/二级，parent_id 自关联）
-- ============================================================
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `name`        VARCHAR(50)  NOT NULL                 COMMENT '分类名称',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0       COMMENT '父分类ID（0=一级分类）',
    `sort_order`  INT          DEFAULT 0                COMMENT '排序号',
    `icon`        VARCHAR(255) DEFAULT NULL             COMMENT '分类图标',
    `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态：1=显示 0=隐藏',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================================
-- 8. 商品主表
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '商品ID',
    `name`           VARCHAR(200)   NOT NULL                 COMMENT '商品名称',
    `description`    TEXT           DEFAULT NULL             COMMENT '商品描述',
    `price`          DECIMAL(10,2)  NOT NULL                 COMMENT '销售价格',
    `original_price` DECIMAL(10,2)  DEFAULT NULL             COMMENT '原价（划线价）',
    `stock`          INT            NOT NULL DEFAULT 0       COMMENT '库存数量',
    `status`         TINYINT        NOT NULL DEFAULT 1       COMMENT '状态：1=上架 0=下架',
    `category_id`    BIGINT         NOT NULL                 COMMENT '所属分类ID',
    `main_image`     VARCHAR(255)   DEFAULT NULL             COMMENT '商品主图URL',
    `sales`          INT            NOT NULL DEFAULT 0       COMMENT '销量',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_name` (`name`),
    KEY `idx_sales` (`sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品主表';

-- ============================================================
-- 9. 商品图片附表
-- ============================================================
DROP TABLE IF EXISTS `product_img`;
CREATE TABLE `product_img` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '图片ID',
    `product_id` BIGINT       NOT NULL                 COMMENT '商品ID',
    `img_url`    VARCHAR(255) NOT NULL                 COMMENT '图片URL',
    `sort_order` INT          DEFAULT 0                COMMENT '排序号（数字越小越靠前）',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片附表';

-- ============================================================
-- 10. 购物车表
-- ============================================================
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '购物车项ID',
    `user_id`     BIGINT   NOT NULL                 COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL                 COMMENT '商品ID',
    `quantity`    INT      NOT NULL DEFAULT 1       COMMENT '购买数量',
    `checked`     TINYINT  NOT NULL DEFAULT 1       COMMENT '是否勾选：1=勾选 0=未勾选',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- 11. 用户收货地址表
-- ============================================================
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '地址ID',
    `user_id`       BIGINT       NOT NULL                 COMMENT '用户ID',
    `receiver_name` VARCHAR(50)  NOT NULL                 COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL                 COMMENT '收件人电话',
    `province`      VARCHAR(50)  NOT NULL                 COMMENT '省份',
    `city`          VARCHAR(50)  NOT NULL                 COMMENT '城市',
    `district`      VARCHAR(50)  NOT NULL                 COMMENT '区/县',
    `detail`        VARCHAR(255) NOT NULL                 COMMENT '详细地址',
    `is_default`    TINYINT      NOT NULL DEFAULT 0       COMMENT '是否默认地址：1=是 0=否',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- ============================================================
-- 12. 订单主表
-- ============================================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id`            BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `order_no`      VARCHAR(32)    NOT NULL                 COMMENT '订单号',
    `user_id`       BIGINT         NOT NULL                 COMMENT '用户ID',
    `total_amount`  DECIMAL(10,2)  NOT NULL                 COMMENT '订单总金额',
    `pay_status`    TINYINT        NOT NULL DEFAULT 0       COMMENT '支付状态：0=未支付 1=已支付 2=已退款',
    `ship_status`   TINYINT        NOT NULL DEFAULT 0       COMMENT '物流状态：0=未发货 1=已发货 2=已收货',
    `address_id`    BIGINT         NOT NULL                 COMMENT '收货地址ID',
    `tracking_no`   VARCHAR(50)    DEFAULT NULL             COMMENT '物流单号',
    `remark`        VARCHAR(500)   DEFAULT NULL             COMMENT '订单备注',
    `pay_time`      DATETIME       DEFAULT NULL             COMMENT '支付时间',
    `ship_time`     DATETIME       DEFAULT NULL             COMMENT '发货时间',
    `create_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `update_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_pay_status` (`pay_status`),
    KEY `idx_ship_status` (`ship_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- ============================================================
-- 13. 订单项表
-- ============================================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '订单项ID',
    `order_id`       BIGINT        NOT NULL                 COMMENT '订单ID（关联订单主表）',
    `product_id`     BIGINT        NOT NULL                 COMMENT '商品ID',
    `product_name`   VARCHAR(200)  NOT NULL                 COMMENT '商品名称（快照）',
    `product_img`    VARCHAR(255)  DEFAULT NULL             COMMENT '商品图片（快照）',
    `quantity`       INT           NOT NULL DEFAULT 1       COMMENT '购买数量',
    `unit_price`     DECIMAL(10,2) NOT NULL                 COMMENT '下单时单价',
    `total_price`    DECIMAL(10,2) NOT NULL                 COMMENT '小计金额',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ============================================================
-- 14. 首页轮播图表
-- ============================================================
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '轮播图ID',
    `title`       VARCHAR(100) DEFAULT NULL             COMMENT '标题',
    `img_url`     VARCHAR(255) NOT NULL                 COMMENT '图片URL',
    `link_url`    VARCHAR(255) DEFAULT NULL             COMMENT '跳转链接',
    `sort_order`  INT          DEFAULT 0                COMMENT '排序号',
    `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态：1=启用 0=禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图表';

-- ============================================================
-- 15. 商品收藏表
-- ============================================================
DROP TABLE IF EXISTS `product_collect`;
CREATE TABLE `product_collect` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '收藏ID',
    `user_id`     BIGINT   NOT NULL                 COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL                 COMMENT '商品ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

-- ============================================================
-- 16. 商品评价表
-- ============================================================
DROP TABLE IF EXISTS `product_comment`;
CREATE TABLE `product_comment` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '评价ID',
    `user_id`      BIGINT       NOT NULL                 COMMENT '用户ID',
    `product_id`   BIGINT       NOT NULL                 COMMENT '商品ID',
    `order_id`     BIGINT       NOT NULL                 COMMENT '订单ID',
    `rating`       TINYINT      NOT NULL DEFAULT 5       COMMENT '评分：1~5',
    `content`      VARCHAR(500) DEFAULT NULL             COMMENT '评价内容',
    `pics`         VARCHAR(1000) DEFAULT NULL            COMMENT '评价图片（逗号分隔）',
    `append_content` VARCHAR(500) DEFAULT NULL           COMMENT '追评内容',
    `append_time`    DATETIME    DEFAULT NULL             COMMENT '追评时间',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';


-- ============================================================
-- 初始化数据
-- ============================================================

-- --------------------- 管理员（密码 = MD5("admin123mall_salt_2024")） ---------------------
INSERT INTO `admin` (`username`, `password`, `real_name`) VALUES
('admin', '5fa7aa2efe8fe6e8bb0f1abeda9c8e8a', '超级管理员');

-- --------------------- 角色 ---------------------
INSERT INTO `role` (`name`, `code`, `description`) VALUES
('超级管理员', 'super_admin', '拥有系统全部权限'),
('商品管理员', 'product_admin', '管理商品、分类'),
('订单管理员', 'order_admin', '管理订单、售后');

-- --------------------- 权限（RBAC 菜单树） ---------------------
-- 一级菜单
INSERT INTO `permission` (`name`, `code`, `parent_id`, `type`, `sort_order`) VALUES
('仪表盘',       'dashboard',        0, 1, 1),
('商品管理',     'product',          0, 1, 2),
('订单管理',     'order',            0, 1, 3),
('用户管理',     'user',             0, 1, 4),
('系统管理',     'system',           0, 1, 5),
-- 商品管理子菜单
('商品列表',     'product:list',     2, 1, 1),
('商品分类',     'category:list',    2, 1, 2),
-- 商品操作按钮
('新增商品',     'product:add',      2, 2, 3),
('修改商品',     'product:update',   2, 2, 4),
('删除商品',     'product:delete',   2, 2, 5),
-- 订单管理子菜单
('订单列表',     'order:list',       3, 1, 1),
-- 订单操作按钮
('订单发货',     'order:ship',       3, 2, 2),
('处理售后',     'order:refund',     3, 2, 3),
-- 用户管理子菜单
('用户列表',     'user:list',        4, 1, 1),
('用户禁用',     'user:disable',     4, 2, 2),
-- 系统管理子菜单
('角色管理',     'role:list',        5, 1, 1),
('管理员列表',   'admin:list',       5, 1, 2);

-- --------------------- 管理员-角色关联（admin 拥有 super_admin 角色） ---------------------
INSERT INTO `admin_role` (`admin_id`, `role_id`) VALUES (1, 1);

-- --------------------- 角色-权限关联（super_admin 拥有所有权限） ---------------------
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission`;

-- 商品管理员权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `permission` WHERE `code` IN (
    'product', 'product:list', 'category:list', 'product:add', 'product:update', 'product:delete'
);

-- 订单管理员权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `permission` WHERE `code` IN (
    'order', 'order:list', 'order:ship', 'order:refund'
);

-- --------------------- 商品分类 ---------------------
INSERT INTO `product_category` (`id`, `name`, `parent_id`, `sort_order`) VALUES
(1, '手机数码',  0, 1),
(2, '电脑办公',  0, 2),
(3, '家用电器',  0, 3),
(4, '服装鞋帽',  0, 4),
(5, '食品生鲜',  0, 5),
-- 手机数码 子分类
(6, '智能手机',   1, 1),
(7, '平板电脑',   1, 2),
(8, '智能手表',   1, 3),
-- 电脑办公 子分类
(9, '笔记本',     2, 1),
(10,'台式机',    2, 2),
-- 家用电器 子分类
(11,'空调',      3, 1),
(12,'洗衣机',    3, 2);

-- --------------------- 示例商品（手机数码） ---------------------
INSERT INTO `product` (`name`, `description`, `price`, `original_price`, `stock`, `status`, `category_id`, `main_image`, `sales`) VALUES
('华为Mate 60 Pro', '麒麟9000S芯片 | 卫星通话 | 超可靠玄武架构', 6999.00, 7999.00, 100, 1, 6, '/upload/product/mate60.jpg', 256),
('iPhone 15 Pro Max', 'A17 Pro芯片 | 钛金属设计 | 4800万主摄', 9999.00, 10999.00, 80, 1, 6, '/upload/product/iphone15.jpg', 189),
('小米14 Ultra', '骁龙8Gen3 | 徕卡光学 | 1英寸大底', 5999.00, 6499.00, 120, 1, 6, '/upload/product/mi14u.jpg', 312),
('iPad Air 6', 'M2芯片 | Liquid视网膜屏 | 支持Apple Pencil', 4799.00, 4999.00, 60, 1, 7, '/upload/product/ipadair.jpg', 98),
('MacBook Pro 14', 'M3 Pro芯片 | 18小时续航 | Liquid Retina XDR', 14999.00, 15999.00, 30, 1, 9, '/upload/product/macbook.jpg', 45);

-- --------------------- 首页轮播图 ---------------------
INSERT INTO `banner` (`title`, `img_url`, `link_url`, `sort_order`) VALUES
('618年中大促',     '/upload/banner/banner1.jpg', '/product/list?keyword=618', 1),
('手机超级新品季',  '/upload/banner/banner2.jpg', '/product/list?categoryId=6', 2),
('笔记本特惠专场',  '/upload/banner/banner3.jpg', '/product/list?categoryId=9', 3);

-- --------------------- 测试用户（密码 = MD5("testmall_salt_2024")）---------------------
INSERT INTO `user` (`username`, `password`, `email`, `phone`) VALUES
('test', '8869b9ea05d029277b1c96a1a346d014', 'test@mall.com', '13800138000');
