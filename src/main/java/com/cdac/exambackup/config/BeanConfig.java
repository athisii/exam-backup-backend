package com.cdac.exambackup.config;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author athisii
 * @version 1.0
 * @since 5/3/24
 */

@Configuration
public class BeanConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE) // to change property naming automatically
                .build();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setBase64Variant(Base64Variants.MIME_NO_LINEFEEDS); // no linebreak for base64 encoded data.
        return objectMapper;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Exam Backup API")
                .description("Exam Backup API Documentation")
                .version("v1.0"));
    }

}
