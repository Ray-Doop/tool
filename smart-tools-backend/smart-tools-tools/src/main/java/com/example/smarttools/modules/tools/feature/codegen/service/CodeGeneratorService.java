/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/codegen/service/CodeGeneratorService.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class CodeGeneratorService
 * 注解：Service、Data、Entity、Id、GeneratedValue、RestController、RequestMapping、GetMapping、PostMapping
 * 依赖：com.example.smarttools.modules.tools.feature.codegen.controller.CodeGeneratorController.GenerateRequest、com.example.smarttools.modules.tools.feature.codegen.controller.CodeGeneratorController.FieldSpec、org.springframework.stereotype.Service、java.io.ByteArrayOutputStream、java.io.IOException、java.nio.charset.StandardCharsets、java.util.zip.ZipEntry、java.util.zip.ZipOutputStream、jakarta.persistence.Entity、jakarta.persistence.GeneratedValue、jakarta.persistence.GenerationType、jakarta.persistence.Id、lombok.Data、%s.domain.%s
 * 公开方法：generateProject(GenerateRequest req)；main(String[] args)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.codegen.service;

import com.example.smarttools.modules.tools.feature.codegen.controller.CodeGeneratorController.GenerateRequest;
import com.example.smarttools.modules.tools.feature.codegen.controller.CodeGeneratorController.FieldSpec;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CodeGeneratorService {

    public byte[] generateProject(GenerateRequest req) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            String baseDir = req.artifactId() + "/";
            String packagePath = req.packageName().replace('.', '/') + "/";
            String mainPath = baseDir + "src/main/java/" + packagePath;
            String testPath = baseDir + "src/test/java/" + packagePath;
            String resourcePath = baseDir + "src/main/resources/";

            // pom.xml
            addFile(zos, baseDir + "pom.xml", generatePom(req));

            // Application.java
            String appClassName = capitalize(toCamelCase(req.artifactId())) + "Application";
            addFile(zos, mainPath + appClassName + ".java", generateApplicationClass(req, appClassName));

            // application.yml
            addFile(zos, resourcePath + "application.yml", generateApplicationYml(req));

            // Optional: Entity CRUD
            if (req.entityName() != null && !req.entityName().isBlank()) {
                generateCrud(zos, mainPath, req);
            }

            return baos.toByteArray();
        }
    }

    private void addFile(ZipOutputStream zos, String path, String content) throws IOException {
        zos.putNextEntry(new ZipEntry(path));
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private void generateCrud(ZipOutputStream zos, String mainPath, GenerateRequest req) throws IOException {
        String entity = req.entityName();
        String lowerEntity = entity.toLowerCase();
        
        // Entity
        StringBuilder fields = new StringBuilder();
        for (FieldSpec f : req.fields()) {
            fields.append("    private %s %s;\n".formatted(f.type(), f.name()));
        }
        String entityContent = """
            package %s.domain;
            
            import jakarta.persistence.Entity;
            import jakarta.persistence.GeneratedValue;
            import jakarta.persistence.GenerationType;
            import jakarta.persistence.Id;
            import lombok.Data;
            
            @Data
            @Entity
            public class %s {
                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                private Long id;
                
            %s
            }
            """.formatted(req.packageName(), entity, fields.toString());
        addFile(zos, mainPath + "domain/" + entity + ".java", entityContent);

        // Repository
        String repoContent = """
            package %s.repository;
            
            import %s.domain.%s;
            import org.springframework.data.jpa.repository.JpaRepository;
            
            public interface %sRepository extends JpaRepository<%s, Long> {
            }
            """.formatted(req.packageName(), req.packageName(), entity, entity, entity);
        addFile(zos, mainPath + "repository/" + entity + "Repository.java", repoContent);
        
        // Service
        String serviceContent = """
            package %s.service;
            
            import %s.domain.%s;
            import %s.repository.%sRepository;
            import org.springframework.stereotype.Service;
            import java.util.List;
            
            @Service
            public class %sService {
                private final %sRepository repository;
                
                public %sService(%sRepository repository) {
                    this.repository = repository;
                }
                
                public List<%s> findAll() {
                    return repository.findAll();
                }
                
                public %s save(%s entity) {
                    return repository.save(entity);
                }
            }
            """.formatted(req.packageName(), req.packageName(), entity, req.packageName(), entity, entity, entity, entity, entity, entity, entity);
        addFile(zos, mainPath + "service/" + entity + "Service.java", serviceContent);
        
        // Controller
        String controllerContent = """
            package %s.controller;
            
            import %s.domain.%s;
            import %s.service.%sService;
            import org.springframework.web.bind.annotation.*;
            import java.util.List;
            
            @RestController
            @RequestMapping("/api/%ss")
            public class %sController {
                private final %sService service;
                
                public %sController(%sService service) {
                    this.service = service;
                }
                
                @GetMapping
                public List<%s> list() {
                    return service.findAll();
                }
                
                @PostMapping
                public %s create(@RequestBody %s entity) {
                    return service.save(entity);
                }
            }
            """.formatted(req.packageName(), req.packageName(), entity, req.packageName(), entity, lowerEntity, entity, entity, entity, entity, entity, entity, entity);
        addFile(zos, mainPath + "controller/" + entity + "Controller.java", controllerContent);
    }

    private String generatePom(GenerateRequest req) {
        StringBuilder deps = new StringBuilder();
        if (req.dependencies().contains("web")) {
            deps.append("""
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-web</artifactId>
                    </dependency>
                """);
        }
        if (req.dependencies().contains("jpa")) {
            deps.append("""
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-data-jpa</artifactId>
                    </dependency>
                """);
        }
        if (req.dependencies().contains("mysql")) {
            deps.append("""
                    <dependency>
                        <groupId>com.mysql</groupId>
                        <artifactId>mysql-connector-j</artifactId>
                        <scope>runtime</scope>
                    </dependency>
                """);
        }
        if (req.dependencies().contains("lombok")) {
            deps.append("""
                    <dependency>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <optional>true</optional>
                    </dependency>
                """);
        }
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                <parent>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-parent</artifactId>
                    <version>3.2.0</version>
                    <relativePath/> <!-- lookup parent from repository -->
                </parent>
                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                <name>%s</name>
                <description>Demo project for Spring Boot</description>
                <properties>
                    <java.version>%s</java.version>
                </properties>
                <dependencies>
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter</artifactId>
                    </dependency>
                    %s
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-test</artifactId>
                        <scope>test</scope>
                    </dependency>
                </dependencies>
                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """.formatted(req.groupId(), req.artifactId(), req.artifactId(), req.javaVersion(), deps.toString());
    }

    private String generateApplicationClass(GenerateRequest req, String className) {
        return """
            package %s;
            
            import org.springframework.boot.SpringApplication;
            import org.springframework.boot.autoconfigure.SpringBootApplication;
            
            @SpringBootApplication
            public class %s {
                public static void main(String[] args) {
                    SpringApplication.run(%s.class, args);
                }
            }
            """.formatted(req.packageName(), className, className);
    }

    private String generateApplicationYml(GenerateRequest req) {
        return """
            spring:
              application:
                name: %s
            """.formatted(req.artifactId());
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    private String toCamelCase(String s) {
        String[] parts = s.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(capitalize(part));
        }
        return sb.toString();
    }
}
