<!--
  @generated-file-note
  文件：smart-tools-backend/README.md
  用途：说明文档
  用途：项目说明/设计文档
  维护：与代码/配置变更保持一致，避免文档与实现脱节
-->
# Smart Tools Backend (Maven 多模块)

## 模块说明

- `smart-tools-common`：统一返回体
- `smart-tools-domain`：JPA 实体与 Repository
- `smart-tools-security`：JWT 鉴权与 Spring Security 配置
- `smart-tools-api`：Spring Boot Web API（控制器/服务/启动类）

## 本地运行（H2 内存库）

```bash
cd C:\Users\30274\Desktop\xxm\smart-tools-backend
mvn -pl smart-tools-api -am spring-boot:run
```

默认：

- API：`http://localhost:8080`
- H2 Console：`http://localhost:8080/h2-console`

## 使用 MySQL

1) 执行建表 SQL：[mysql_schema.sql](file:///c:/Users/30274/Desktop/xxm/smart-tools-backend/db/mysql_schema.sql)

2) 按需修改 [application-mysql.yml](file:///c:/Users/30274/Desktop/xxm/smart-tools-backend/smart-tools-api/src/main/resources/application-mysql.yml) 的连接信息

3) 启动时启用 profile：

```bash
cd C:\Users\30274\Desktop\xxm\smart-tools-backend
mvn -pl smart-tools-api -am spring-boot:run -Dspring-boot.run.profiles=mysql
```

## 环境变量

- `JWT_SECRET`：JWT 密钥（至少 32 字符）
- `CORS_ALLOWED_ORIGINS`：允许跨域的前端地址（默认 `http://localhost:5173`，支持逗号分隔）
- `MYSQL_URL` / `MYSQL_USERNAME` / `MYSQL_PASSWORD`：MySQL 连接信息（profile=mysql）
- `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` / `REDIS_DB`：Redis 连接信息（profile=mysql）
- `RABBITMQ_HOST` / `RABBITMQ_PORT` / `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD`：RabbitMQ 连接信息（profile=mysql）
- `MAIL_HOST` / `MAIL_PORT` / `MAIL_USERNAME` / `MAIL_PASSWORD` / `MAIL_FROM`：SMTP 邮件发送配置
- `WXLOGIN_APP_ID` / `WXLOGIN_APP_SECRET`：微信登录配置
