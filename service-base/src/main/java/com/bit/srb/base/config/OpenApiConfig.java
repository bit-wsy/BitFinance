package com.bit.srb.base.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class OpenApiConfig implements WebMvcConfigurer {
    // 给文档分组
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("springshop-public")
                .pathsToMatch("/public/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("Public API")
                                .description("Public API Documentation")
                                .version("v0.0.1")
                                .license(new License().name("Shell").url("http://springdoc.org"))))//联系人信息
                .build();
    }
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("springshop-admin")
                .pathsToMatch("/admin/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                        .title("Adimin API")
                        .description("Adimin API Documentation")
                        .version("v0.0.1")
                        .license(new License().name("Shell").url("http://springdoc.org"))))//联系人信息
//                .addOpenApiMethodFilter(method -> method.isAnnotationPresent(Admin.class))
                .build();
    }

    @Bean
    public GroupedOpenApi webApi() {
        return GroupedOpenApi.builder()
                .group("springshop-api")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("Web API")
                                .description("Web API Documentation")
                                .version("v0.0.1")
                                .license(new License().name("Shell").url("http://springdoc.org"))))//联系人信息
//                .addOpenApiMethodFilter(method -> method.isAnnotationPresent(Admin.class))
                .build();
    }
    // 配置整个文档的信息
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SRB API")
                        .description("All 接口")
                        .version("v0.0.1")
                        .license(new License().name("Shell").url("http://springdoc.org"))) //联系人信息
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"))
                ;
    }



}
