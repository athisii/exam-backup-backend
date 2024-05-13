package com.cdac.exambackup.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * @author athisii
 * @version 1.0
 * @since 5/13/24
 */

@OpenAPIDefinition(
        info = @Info(
                title = "Exam Backup API",
                description = "Exam Backup API Documentation",
                version = "v1.0",
                contact = @Contact(name = "athisii", email = "daiho.ekhe@cdac.in")
        ),
        servers = {
                @Server(description = "Local Env", url = "http://localhost:8080/exam-backup/"),
                @Server(description = "Prod ENV", url = "https://exam-backup/api/")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
