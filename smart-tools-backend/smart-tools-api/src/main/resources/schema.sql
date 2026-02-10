-- @generated-file-note
-- 文件：smart-tools-backend/smart-tools-api/src/main/resources/schema.sql
-- 用途：数据库结构/迁移/初始化脚本
-- 归属：后端 smart-tools-api
-- 分层：resources
-- 用途：用于初始化/迁移数据库结构与基础数据
-- 安全：不要在脚本中写入生产口令、密钥或真实个人数据
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名（唯一）',
    display_name VARCHAR(64) COMMENT '显示名称/昵称',
    avatar_url VARCHAR(512) COMMENT '头像URL',
    bio VARCHAR(512) COMMENT '个人简介',
    email VARCHAR(128) UNIQUE COMMENT '邮箱地址（唯一）',
    phone VARCHAR(32) UNIQUE COMMENT '手机号（唯一）',
    wechat_openid VARCHAR(64) UNIQUE COMMENT '微信OpenID（唯一）',
    password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希值（BCrypt）',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账户状态：true启用 false禁用',
    registration_completed BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否完成注册',
    preferences_json VARCHAR(4096) COMMENT '用户偏好设置（JSON格式）',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE TABLE IF NOT EXISTS sys_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联 sys_user.id',
    role_code VARCHAR(64) COMMENT '角色编码（轻量标识）',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '管理员启用状态',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE INDEX idx_sys_admin_user_id ON sys_admin(user_id);

CREATE TABLE IF NOT EXISTS sys_tool (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    slug VARCHAR(64) NOT NULL UNIQUE COMMENT '工具唯一标识（URL路径）',
    name VARCHAR(128) NOT NULL COMMENT '工具名称',
    category VARCHAR(64) NOT NULL COMMENT '所属分类',
    description VARCHAR(512) COMMENT '工具描述',
    keywords VARCHAR(512) COMMENT '搜索关键词（逗号分隔）',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '启用状态：true启用 false禁用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序权重（越小越靠前）',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE INDEX idx_sys_tool_category ON sys_tool(category);
CREATE INDEX idx_sys_tool_enabled ON sys_tool(enabled);

CREATE TABLE IF NOT EXISTS user_tool_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tool_slug VARCHAR(64) NOT NULL COMMENT '工具标识',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    CONSTRAINT uk_user_tool_favorite UNIQUE (user_id, tool_slug),
    CONSTRAINT fk_user_tool_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE INDEX idx_user_tool_favorite_user_id ON user_tool_favorite(user_id);

CREATE TABLE IF NOT EXISTS user_tool_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tool_slug VARCHAR(64) NOT NULL COMMENT '工具标识',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间'
);

CREATE UNIQUE INDEX uk_user_tool_like ON user_tool_like(user_id, tool_slug);
CREATE INDEX idx_user_tool_like_user_id ON user_tool_like(user_id);
CREATE INDEX idx_user_tool_like_tool_slug ON user_tool_like(tool_slug);

CREATE TABLE IF NOT EXISTS tool_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tool_slug VARCHAR(64) NOT NULL COMMENT '工具标识',
    parent_id BIGINT COMMENT '父评论ID',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE INDEX idx_tool_comment_tool_slug ON tool_comment(tool_slug);
CREATE INDEX idx_tool_comment_user_id ON tool_comment(user_id);
CREATE INDEX idx_tool_comment_parent_id ON tool_comment(parent_id);

CREATE TABLE IF NOT EXISTS tool_comment_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间'
);

CREATE UNIQUE INDEX uk_tool_comment_like ON tool_comment_like(user_id, comment_id);
CREATE INDEX idx_tool_comment_like_user_id ON tool_comment_like(user_id);
CREATE INDEX idx_tool_comment_like_comment_id ON tool_comment_like(comment_id);

CREATE TABLE IF NOT EXISTS sys_visit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID（匿名访问为NULL）',
    tool_slug VARCHAR(64) COMMENT '访问的工具标识',
    path VARCHAR(256) COMMENT '访问路径',
    ip VARCHAR(64) COMMENT '客户端IP',
    user_agent VARCHAR(512) COMMENT '浏览器User-Agent',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间'
);

CREATE INDEX idx_sys_visit_log_user_id ON sys_visit_log(user_id);
CREATE INDEX idx_sys_visit_log_tool_slug ON sys_visit_log(tool_slug);

CREATE TABLE IF NOT EXISTS sys_module (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '模块编码',
    name VARCHAR(128) NOT NULL COMMENT '模块名称',
    description VARCHAR(512) COMMENT '模块描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '启用状态：true启用 false禁用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE TABLE IF NOT EXISTS sys_function (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    module_id BIGINT NOT NULL COMMENT '模块ID',
    code VARCHAR(64) NOT NULL COMMENT '功能编码',
    name VARCHAR(128) NOT NULL COMMENT '功能名称',
    description VARCHAR(512) COMMENT '功能描述',
    endpoint VARCHAR(256) COMMENT '接口路径',
    method VARCHAR(16) COMMENT 'HTTP 方法',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '启用状态：true启用 false禁用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE INDEX idx_sys_function_module_id ON sys_function(module_id);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '角色编码',
    name VARCHAR(128) NOT NULL COMMENT '角色名称',
    description VARCHAR(512) COMMENT '角色描述',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '启用状态：true启用 false禁用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '权限编码',
    name VARCHAR(128) NOT NULL COMMENT '权限名称',
    type VARCHAR(32) NOT NULL COMMENT '权限类型',
    parent_id BIGINT COMMENT '父级权限',
    path VARCHAR(256) COMMENT '路径',
    method VARCHAR(16) COMMENT 'HTTP 方法',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '启用状态：true启用 false禁用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID'
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID'
);

CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    module VARCHAR(128) COMMENT '模块',
    action VARCHAR(128) COMMENT '动作',
    target VARCHAR(256) COMMENT '目标',
    result VARCHAR(64) COMMENT '结果',
    ip VARCHAR(64) COMMENT 'IP',
    user_agent VARCHAR(512) COMMENT 'User-Agent',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

CREATE TABLE IF NOT EXISTS sys_error_tip (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '错误码',
    title VARCHAR(128) NOT NULL COMMENT '标题',
    message VARCHAR(512) NOT NULL COMMENT '提示内容',
    severity VARCHAR(32) NOT NULL COMMENT '级别',
    solution VARCHAR(512) COMMENT '解决方案',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '启用状态：true启用 false禁用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

CREATE TABLE IF NOT EXISTS sys_error_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    source VARCHAR(32) NOT NULL COMMENT '来源',
    level VARCHAR(16) NOT NULL COMMENT '级别',
    code VARCHAR(64) COMMENT '错误码',
    message VARCHAR(512) NOT NULL COMMENT '错误信息',
    detail VARCHAR(2000) COMMENT '错误详情',
    path VARCHAR(256) COMMENT '请求路径',
    method VARCHAR(16) COMMENT '请求方法',
    page_url VARCHAR(256) COMMENT '页面URL',
    stack VARCHAR(2000) COMMENT '堆栈',
    ip VARCHAR(64) COMMENT 'IP',
    user_agent VARCHAR(256) COMMENT 'User-Agent',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

CREATE TABLE IF NOT EXISTS sys_server_metric (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    host VARCHAR(128) COMMENT '主机标识',
    cpu_usage DOUBLE COMMENT 'CPU 使用率',
    mem_usage DOUBLE COMMENT '内存使用率',
    disk_usage DOUBLE COMMENT '磁盘使用率',
    thread_count INT COMMENT '线程数',
    qps DOUBLE COMMENT 'QPS',
    rt_p95 DOUBLE COMMENT 'P95 响应时间',
    collected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '采集时间'
);

INSERT INTO sys_module (code, name, description, sort_order, enabled)
SELECT 'system', '系统管理', '系统基础能力与配置', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_module WHERE code = 'system');
INSERT INTO sys_module (code, name, description, sort_order, enabled)
SELECT 'user', '用户管理', '用户账号与资料维护', 2, TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_module WHERE code = 'user');
INSERT INTO sys_module (code, name, description, sort_order, enabled)
SELECT 'security', '权限管理', '角色与权限配置', 3, TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_module WHERE code = 'security');

INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'users.list', '用户列表', '查看用户列表', '/api/admin/users', 'GET', TRUE
FROM sys_module m
WHERE m.code = 'user'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'users.list');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'users.update', '用户更新', '更新用户状态与密码', '/api/admin/users/{id}', 'PUT', TRUE
FROM sys_module m
WHERE m.code = 'user'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'users.update');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'modules.list', '模块列表', '查看模块列表', '/api/admin/modules', 'GET', TRUE
FROM sys_module m
WHERE m.code = 'system'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'modules.list');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'functions.list', '功能列表', '查看功能列表', '/api/admin/functions', 'GET', TRUE
FROM sys_module m
WHERE m.code = 'system'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'functions.list');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'roles.list', '角色列表', '查看角色列表', '/api/admin/roles', 'GET', TRUE
FROM sys_module m
WHERE m.code = 'security'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'roles.list');
INSERT INTO sys_function (module_id, code, name, description, endpoint, method, enabled)
SELECT m.id, 'permissions.list', '权限列表', '查看权限列表', '/api/admin/permissions', 'GET', TRUE
FROM sys_module m
WHERE m.code = 'security'
  AND NOT EXISTS (SELECT 1 FROM sys_function WHERE code = 'permissions.list');

INSERT INTO sys_role (code, name, description, enabled)
SELECT 'admin', '超级管理员', '拥有全部管理权限', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'admin');
INSERT INTO sys_role (code, name, description, enabled)
SELECT 'ops', '运维管理员', '负责监控与日志管理', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'ops');
INSERT INTO sys_role (code, name, description, enabled)
SELECT 'editor', '内容管理员', '负责内容维护', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'editor');

INSERT INTO sys_permission (code, name, type, parent_id, path, method, enabled)
SELECT 'admin.dashboard', '后台概览', 'MENU', NULL, '/admin', 'GET', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'admin.dashboard');
INSERT INTO sys_permission (code, name, type, parent_id, path, method, enabled)
SELECT 'admin.users', '用户管理', 'MENU', NULL, '/admin/users', 'GET', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'admin.users');
INSERT INTO sys_permission (code, name, type, parent_id, path, method, enabled)
SELECT 'admin.roles', '角色管理', 'MENU', NULL, '/admin/roles', 'GET', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'admin.roles');
INSERT INTO sys_permission (code, name, type, parent_id, path, method, enabled)
SELECT 'admin.permissions', '权限管理', 'MENU', NULL, '/admin/permissions', 'GET', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'admin.permissions');

INSERT INTO sys_error_tip (code, title, message, severity, solution, enabled)
SELECT 'INTERNAL_ERROR', '系统错误', '系统内部错误，请稍后重试', 'error', '请联系管理员或查看错误日志', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_error_tip WHERE code = 'INTERNAL_ERROR');
INSERT INTO sys_error_tip (code, title, message, severity, solution, enabled)
SELECT 'UNAUTHORIZED', '未登录', '登录状态已失效', 'warn', '请重新登录', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_error_tip WHERE code = 'UNAUTHORIZED');
INSERT INTO sys_error_tip (code, title, message, severity, solution, enabled)
SELECT 'FORBIDDEN', '无权限', '当前账号无权限访问该资源', 'warn', '请联系管理员开通权限', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sys_error_tip WHERE code = 'FORBIDDEN');
