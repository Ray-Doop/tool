-- @generated-file-note
-- 文件：smart-tools-backend/db/mysql_migrate_v2_add_user_fields.sql
-- 用途：数据库结构/迁移/初始化脚本
-- 用途：用于初始化/迁移数据库结构与基础数据
-- 安全：不要在脚本中写入生产口令、密钥或真实个人数据
ALTER TABLE sys_user
  ADD COLUMN email VARCHAR(128) NULL,
  ADD COLUMN phone VARCHAR(32) NULL,
  ADD COLUMN wechat_openid VARCHAR(64) NULL,
  ADD COLUMN display_name VARCHAR(64) NULL,
  ADD COLUMN avatar_url VARCHAR(512) NULL,
  ADD COLUMN bio VARCHAR(512) NULL,
  ADD COLUMN preferences_json VARCHAR(4096) NULL;

ALTER TABLE sys_user
  ADD UNIQUE KEY uk_sys_user_email (email),
  ADD UNIQUE KEY uk_sys_user_phone (phone),
  ADD UNIQUE KEY uk_sys_user_wechat_openid (wechat_openid);
