package gpl.karina.resource.config;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${app.profileUrl}")
    private String profileUrl;

    @Value("${app.financeUrl}")
    private String financeUrl;

    @Value("${app.projectUrl}")
    private String projectUrl;

    @Value("${app.purchaseUrl}")
    private String purchaseUrl;

    @Value("${app.resourceUrl}")
    private String resourceUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins(profileUrl, financeUrl, projectUrl, purchaseUrl, resourceUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
