package gpl.karina.resource.config;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${resource.app.profileUrl}")
    private String profileUrl;

    @Value("${resource.app.financeUrl}")
    private String financeUrl;

    @Value("${resource.app.projectUrl}")
    private String projectUrl;

    @Value("${resource.app.purchaseUrl}")
    private String purchaseUrl;

    @Value("${resource.app.resourceUrl}")
    private String resourceUrl;

    @Value("${resource.app.frontendUrl}")
    private String frontendUrl;
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins(frontendUrl,profileUrl, financeUrl, projectUrl, purchaseUrl, resourceUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
