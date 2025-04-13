package gpl.karina.purchase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${purchase.app.profileUrl}")
    private String profileUrl;

    @Value("${purchase.app.financeUrl}")
    private String financeUrl;

    @Value("${purchase.app.projectUrl}")
    private String projectUrl;

    @Value("${purchase.app.purchaseUrl}")
    private String purchaseUrl;

    @Value("${purchase.app.resourceUrl}")
    private String resourceUrl;

    @Value("${purchase.app.frontendUrl}")
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
