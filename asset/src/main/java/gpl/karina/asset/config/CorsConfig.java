package gpl.karina.asset.config;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${asset.app.profileUrl}")
    private String profileUrl;

    @Value("${asset.app.financeUrl}")
    private String financeUrl;

    @Value("${asset.app.projectUrl}")
    private String projectUrl;

    @Value("${asset.app.purchaseUrl}")
    private String purchaseUrl;

    @Value("${asset.app.resourceUrl}")
    private String resourceUrl;

    @Value("${asset.app.frontendUrl}")
    private String frontendUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(profileUrl, financeUrl, projectUrl, purchaseUrl, resourceUrl, frontendUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
