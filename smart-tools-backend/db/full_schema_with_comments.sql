-- @generated-file-note
-- 文件：smart-tools-backend/db/full_schema_with_comments.sql
-- 用途：数据库结构/迁移/初始化脚本
-- 用途：用于初始化/迁移数据库结构与基础数据
-- 安全：不要在脚本中写入生产口令、密钥或真实个人数据
/*
 * Smart Tools 数据库全量结构脚本
 *
 * 包含表：
 * 1. sys_user: 用户表
 * 2. sys_tool: 工具表
 * 3. user_tool_favorite: 用户收藏表
 * 4. sys_visit_log: 访问日志表
 *
 * 注意：本脚本适用于 MySQL 8.0+
 */

CREATE DATABASE IF NOT EXISTS smart_tools DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE smart_tools;

-- -----------------------------------------------------
-- Table structure for table `sys_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  username VARCHAR(64) NOT NULL COMMENT '用户名（唯一）',
  display_name VARCHAR(64) NULL COMMENT '显示名称/昵称',
  avatar_url VARCHAR(512) NULL COMMENT '头像URL',
  bio VARCHAR(512) NULL COMMENT '个人简介',
  email VARCHAR(128) NULL COMMENT '邮箱地址（唯一）',
  phone VARCHAR(32) NULL COMMENT '手机号（唯一）',
  wechat_openid VARCHAR(64) NULL COMMENT '微信OpenID（唯一）',
  password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希值（BCrypt）',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户状态：1启用 0禁用',
  preferences_json VARCHAR(4096) NULL COMMENT '用户偏好设置（JSON格式）',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_username (username),
  UNIQUE KEY uk_sys_user_email (email),
  UNIQUE KEY uk_sys_user_phone (phone),
  UNIQUE KEY uk_sys_user_wechat_openid (wechat_openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -----------------------------------------------------
-- Table structure for table `sys_tool`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_tool (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  slug VARCHAR(64) NOT NULL COMMENT '工具唯一标识（URL路径）',
  name VARCHAR(128) NOT NULL COMMENT '工具名称',
  category VARCHAR(64) NOT NULL COMMENT '所属分类',
  description VARCHAR(512) NULL COMMENT '工具描述',
  keywords VARCHAR(512) NULL COMMENT '搜索关键词（逗号分隔）',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '启用状态：1启用 0禁用',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序权重（越小越靠前）',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_tool_slug (slug),
  KEY idx_sys_tool_category (category),
  KEY idx_sys_tool_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具表';

-- -----------------------------------------------------
-- Table structure for table `user_tool_favorite`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS user_tool_favorite (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID（关联sys_user.id）',
  tool_slug VARCHAR(64) NOT NULL COMMENT '工具标识（关联sys_tool.slug）',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_tool_favorite (user_id, tool_slug),
  KEY idx_user_tool_favorite_user_id (user_id),
  CONSTRAINT fk_user_tool_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏工具表';

-- -----------------------------------------------------
-- Table structure for table `user_tool_like`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS user_tool_like (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID（关联sys_user.id）',
  tool_slug VARCHAR(64) NOT NULL COMMENT '工具标识',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_tool_like (user_id, tool_slug),
  KEY idx_user_tool_like_user_id (user_id),
  KEY idx_user_tool_like_tool_slug (tool_slug),
  CONSTRAINT fk_user_tool_like_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞工具表';

-- -----------------------------------------------------
-- Table structure for table `tool_comment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tool_comment (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID（关联sys_user.id）',
  tool_slug VARCHAR(64) NOT NULL COMMENT '工具标识',
  parent_id BIGINT NULL COMMENT '父评论ID',
  content VARCHAR(1000) NOT NULL COMMENT '评论内容',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_tool_comment_tool_slug (tool_slug),
  KEY idx_tool_comment_user_id (user_id),
  KEY idx_tool_comment_parent_id (parent_id),
  CONSTRAINT fk_tool_comment_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
  CONSTRAINT fk_tool_comment_parent FOREIGN KEY (parent_id) REFERENCES tool_comment(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具评论表';

-- -----------------------------------------------------
-- Table structure for table `tool_comment_like`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tool_comment_like (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID（关联sys_user.id）',
  comment_id BIGINT NOT NULL COMMENT '评论ID（关联tool_comment.id）',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_tool_comment_like (user_id, comment_id),
  KEY idx_tool_comment_like_user_id (user_id),
  KEY idx_tool_comment_like_comment_id (comment_id),
  CONSTRAINT fk_tool_comment_like_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
  CONSTRAINT fk_tool_comment_like_comment FOREIGN KEY (comment_id) REFERENCES tool_comment(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

-- -----------------------------------------------------
-- Table structure for table `sys_visit_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_visit_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NULL COMMENT '用户ID（匿名访问为NULL）',
  tool_slug VARCHAR(64) NULL COMMENT '访问的工具标识',
  path VARCHAR(256) NULL COMMENT '访问路径',
  ip VARCHAR(64) NULL COMMENT '客户端IP',
  user_agent VARCHAR(512) NULL COMMENT '浏览器User-Agent',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (id),
  KEY idx_sys_visit_log_user_id (user_id),
  KEY idx_sys_visit_log_tool_slug (tool_slug),
  KEY idx_sys_visit_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问日志表';
