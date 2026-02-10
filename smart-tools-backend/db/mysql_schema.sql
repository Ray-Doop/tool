-- @generated-file-note
-- 文件：smart-tools-backend/db/mysql_schema.sql
-- 用途：数据库结构/迁移/初始化脚本
-- 用途：用于初始化/迁移数据库结构与基础数据
-- 安全：不要在脚本中写入生产口令、密钥或真实个人数据
/*
 * Smart Tools 数据库全量结构脚本 (MySQL)
 *
 * 包含表：
 * 1. sys_user: 用户表
 * 2. sys_tool: 工具表
 * 3. user_tool_favorite: 用户收藏表
 * 4. sys_visit_log: 访问日志表
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
  registration_completed TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否完成注册',
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
-- Table structure for table `sys_admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_admin (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID（关联 sys_user.id）',
  role_code VARCHAR(64) NULL COMMENT '角色编码（轻量标识）',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '启用状态：1启用 0禁用',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_admin_user_id (user_id),
  KEY idx_sys_admin_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表（密码与账号信息沿用 sys_user）';

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

-- -----------------------------------------------------
-- Table structure for table `sys_module`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_module (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  code VARCHAR(64) NOT NULL COMMENT '模块编码',
  name VARCHAR(128) NOT NULL COMMENT '模块名称',
  description VARCHAR(512) NULL COMMENT '模块描述',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '启用状态',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_module_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统模块表';

-- -----------------------------------------------------
-- Table structure for table `sys_function`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_function (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  module_id BIGINT NOT NULL COMMENT '模块ID',
  code VARCHAR(64) NOT NULL COMMENT '功能编码',
  name VARCHAR(128) NOT NULL COMMENT '功能名称',
  description VARCHAR(512) NULL COMMENT '功能描述',
  endpoint VARCHAR(256) NULL COMMENT '接口路径',
  method VARCHAR(16) NULL COMMENT 'HTTP 方法',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '启用状态',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_sys_function_module_id (module_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功能表';

-- -----------------------------------------------------
-- Table structure for table `sys_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  code VARCHAR(64) NOT NULL COMMENT '角色编码',
  name VARCHAR(128) NOT NULL COMMENT '角色名称',
  description VARCHAR(512) NULL COMMENT '角色描述',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '启用状态',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- -----------------------------------------------------
-- Table structure for table `sys_permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  code VARCHAR(64) NOT NULL COMMENT '权限编码',
  name VARCHAR(128) NOT NULL COMMENT '权限名称',
  type VARCHAR(32) NOT NULL COMMENT '权限类型',
  parent_id BIGINT NULL COMMENT '父级权限',
  path VARCHAR(256) NULL COMMENT '路径',
  method VARCHAR(16) NULL COMMENT 'HTTP 方法',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '启用状态',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_permission_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- -----------------------------------------------------
-- Table structure for table `sys_role_permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (id),
  KEY idx_sys_role_permission_role_id (role_id),
  KEY idx_sys_role_permission_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关系表';

-- -----------------------------------------------------
-- Table structure for table `sys_user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (id),
  KEY idx_sys_user_role_user_id (user_id),
  KEY idx_sys_user_role_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';

-- -----------------------------------------------------
-- Table structure for table `sys_operation_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_operation_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NULL COMMENT '用户ID',
  module VARCHAR(128) NULL COMMENT '模块',
  action VARCHAR(128) NULL COMMENT '动作',
  target VARCHAR(256) NULL COMMENT '目标',
  result VARCHAR(64) NULL COMMENT '结果',
  ip VARCHAR(64) NULL COMMENT 'IP',
  user_agent VARCHAR(512) NULL COMMENT 'User-Agent',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_sys_operation_log_user_id (user_id),
  KEY idx_sys_operation_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- -----------------------------------------------------
-- Table structure for table `sys_error_tip`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_error_tip (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  code VARCHAR(64) NOT NULL COMMENT '错误码',
  title VARCHAR(128) NOT NULL COMMENT '标题',
  message VARCHAR(512) NOT NULL COMMENT '提示内容',
  severity VARCHAR(32) NOT NULL COMMENT '级别',
  solution VARCHAR(512) NULL COMMENT '解决方案',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '启用状态',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_error_tip_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错误提示表';

-- -----------------------------------------------------
-- Table structure for table `sys_error_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_error_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  source VARCHAR(32) NOT NULL COMMENT '来源',
  level VARCHAR(16) NOT NULL COMMENT '级别',
  code VARCHAR(64) NULL COMMENT '错误码',
  message VARCHAR(512) NOT NULL COMMENT '错误信息',
  detail VARCHAR(2000) NULL COMMENT '错误详情',
  path VARCHAR(256) NULL COMMENT '请求路径',
  method VARCHAR(16) NULL COMMENT '请求方法',
  page_url VARCHAR(256) NULL COMMENT '页面URL',
  stack VARCHAR(2000) NULL COMMENT '堆栈',
  ip VARCHAR(64) NULL COMMENT 'IP',
  user_agent VARCHAR(256) NULL COMMENT 'User-Agent',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_sys_error_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错误日志表';

-- -----------------------------------------------------
-- Table structure for table `sys_server_metric`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_server_metric (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  host VARCHAR(128) NULL COMMENT '主机标识',
  cpu_usage DOUBLE NULL COMMENT 'CPU 使用率',
  mem_usage DOUBLE NULL COMMENT '内存使用率',
  disk_usage DOUBLE NULL COMMENT '磁盘使用率',
  thread_count INT NULL COMMENT '线程数',
  qps DOUBLE NULL COMMENT 'QPS',
  rt_p95 DOUBLE NULL COMMENT 'P95 响应时间',
  collected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '采集时间',
  PRIMARY KEY (id),
  KEY idx_sys_server_metric_host (host),
  KEY idx_sys_server_metric_collected_at (collected_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务指标表';

INSERT INTO sys_module (code, name, description, sort_order, enabled)
SELECT 'system', '系统管理', '系统基础能力与配置', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_module WHERE code = 'system');
INSERT INTO sys_module (code, name, description, sort_order, enabled)
SELECT 'user', '用户管理', '用户账号与资料维护', 2, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_module WHERE code = 'user');
INSERT INTO sys_module (code, name, description, sort_order, enabled)
SELECT 'security', '权限管理', '角色与权限配置', 3, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_module WHERE code = 'security');

INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'users.list', '用户列表', '查看用户列表', '/api/admin/users', 'GET', 1
FROM sys_module m
WHERE m.code = 'user'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'users.list');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'users.update', '用户更新', '更新用户状态与密码', '/api/admin/users/{id}', 'PUT', 1
FROM sys_module m
WHERE m.code = 'user'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'users.update');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'modules.list', '模块列表', '查看模块列表', '/api/admin/modules', 'GET', 1
FROM sys_module m
WHERE m.code = 'system'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'modules.list');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'functions.list', '功能列表', '查看功能列表', '/api/admin/functions', 'GET', 1
FROM sys_module m
WHERE m.code = 'system'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'functions.list');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'roles.list', '角色列表', '查看角色列表', '/api/admin/roles', 'GET', 1
FROM sys_module m
WHERE m.code = 'security'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'roles.list');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'permissions.list', '权限列表', '查看权限列表', '/api/admin/permissions', 'GET', 1
FROM sys_module m
WHERE m.code = 'security'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'permissions.list');

INSERT INTO sys_role (code, name, description, enabled)
SELECT 'admin', '超级管理员', '拥有全部管理权限', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'admin');
INSERT INTO sys_role (code, name, description, enabled)
SELECT 'ops', '运维管理员', '负责监控与日志管理', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'ops');
INSERT INTO sys_role (code, name, description, enabled)
SELECT 'editor', '内容管理员', '负责内容维护', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'editor');

INSERT INTO sys_permission (code, name, type, parent_id, path, method, enabled)
SELECT 'admin.dashboard', '后台概览', 'MENU', NULL, '/admin', 'GET', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'admin.dashboard');
INSERT INTO sys_permission (code, name, type, parent_id, path, method, enabled)
SELECT 'admin.users', '用户管理', 'MENU', NULL, '/admin/users', 'GET', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'admin.users');
INSERT INTO sys_permission (code, name, type, parent_id, path, method, enabled)
SELECT 'admin.roles', '角色管理', 'MENU', NULL, '/admin/roles', 'GET', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'admin.roles');
INSERT INTO sys_permission (code, name, type, parent_id, path, method, enabled)
SELECT 'admin.permissions', '权限管理', 'MENU', NULL, '/admin/permissions', 'GET', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'admin.permissions');

INSERT INTO sys_error_tip (code, title, message, severity, solution, enabled)
SELECT 'INTERNAL_ERROR', '系统错误', '系统内部错误，请稍后重试', 'error', '请联系管理员或查看错误日志', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_error_tip WHERE code = 'INTERNAL_ERROR');
INSERT INTO sys_error_tip (code, title, message, severity, solution, enabled)
SELECT 'UNAUTHORIZED', '未登录', '登录状态已失效', 'warn', '请重新登录', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_error_tip WHERE code = 'UNAUTHORIZED');
INSERT INTO sys_error_tip (code, title, message, severity, solution, enabled)
SELECT 'FORBIDDEN', '无权限', '当前账号无权限访问该资源', 'warn', '请联系管理员开通权限', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_error_tip WHERE code = 'FORBIDDEN');
