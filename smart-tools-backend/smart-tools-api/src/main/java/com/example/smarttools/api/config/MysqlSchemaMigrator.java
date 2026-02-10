/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/config/MysqlSchemaMigrator.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-api
 * 分层：config
 * 类型：class MysqlSchemaMigrator
 * 注解：Component、Profile、Override
 * 依赖：org.springframework.boot.ApplicationArguments、org.springframework.boot.ApplicationRunner、org.springframework.context.annotation.Profile、org.springframework.jdbc.core.JdbcTemplate、org.springframework.stereotype.Component
 * 公开方法：run(ApplicationArguments args)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.api.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("mysql")
public class MysqlSchemaMigrator implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public MysqlSchemaMigrator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureSysUserTable();
        ensureSysUserColumns();
        ensureSysUserIndexes();
    }

    private void ensureSysUserTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_user (
                  id BIGINT NOT NULL AUTO_INCREMENT,
                  username VARCHAR(64) NOT NULL,
                  display_name VARCHAR(64) NULL,
                  avatar_url VARCHAR(512) NULL,
                  bio VARCHAR(512) NULL,
                  email VARCHAR(128) NULL,
                  phone VARCHAR(32) NULL,
                  wechat_openid VARCHAR(64) NULL,
                  password_hash VARCHAR(100) NOT NULL,
                  enabled TINYINT(1) NOT NULL DEFAULT 1,
                  registration_completed TINYINT(1) NOT NULL DEFAULT 1,
                  preferences_json VARCHAR(4096) NULL,
                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  PRIMARY KEY (id),
                  UNIQUE KEY uk_sys_user_username (username),
                  UNIQUE KEY uk_sys_user_email (email),
                  UNIQUE KEY uk_sys_user_phone (phone),
                  UNIQUE KEY uk_sys_user_wechat_openid (wechat_openid)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
    }

    private void ensureSysUserColumns() {
        ensureColumn("sys_user", "display_name", "ALTER TABLE sys_user ADD COLUMN display_name VARCHAR(64) NULL");
        ensureColumn("sys_user", "avatar_url", "ALTER TABLE sys_user ADD COLUMN avatar_url VARCHAR(512) NULL");
        ensureColumn("sys_user", "bio", "ALTER TABLE sys_user ADD COLUMN bio VARCHAR(512) NULL");
        ensureColumn("sys_user", "email", "ALTER TABLE sys_user ADD COLUMN email VARCHAR(128) NULL");
        ensureColumn("sys_user", "phone", "ALTER TABLE sys_user ADD COLUMN phone VARCHAR(32) NULL");
        ensureColumn("sys_user", "wechat_openid", "ALTER TABLE sys_user ADD COLUMN wechat_openid VARCHAR(64) NULL");
        ensureColumn("sys_user", "preferences_json", "ALTER TABLE sys_user ADD COLUMN preferences_json VARCHAR(4096) NULL");
        ensureColumn("sys_user", "registration_completed", "ALTER TABLE sys_user ADD COLUMN registration_completed TINYINT(1) NOT NULL DEFAULT 1");
    }

    private void ensureSysUserIndexes() {
        ensureIndex("sys_user", "uk_sys_user_email", "ALTER TABLE sys_user ADD UNIQUE KEY uk_sys_user_email (email)");
        ensureIndex("sys_user", "uk_sys_user_phone", "ALTER TABLE sys_user ADD UNIQUE KEY uk_sys_user_phone (phone)");
        ensureIndex("sys_user", "uk_sys_user_wechat_openid", "ALTER TABLE sys_user ADD UNIQUE KEY uk_sys_user_wechat_openid (wechat_openid)");
    }

    private void ensureColumn(String table, String column, String sql) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class,
                table,
                column
        );
        if (count != null && count == 0) {
            jdbcTemplate.execute(sql);
        }
    }

    private void ensureIndex(String table, String index, String sql) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = ? AND index_name = ?",
                Integer.class,
                table,
                index
        );
        if (count != null && count == 0) {
            jdbcTemplate.execute(sql);
        }
    }
}
