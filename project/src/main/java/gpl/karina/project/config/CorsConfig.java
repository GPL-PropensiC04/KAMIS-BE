package gpl.karina.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${project.app.profileUrl}")
    private String profileUrl;

    @Value("${project.app.financeUrl}")
    private String financeUrl;

    @Value("${project.app.projectUrl}")
    private String projectUrl;

    @Value("${project.app.purchaseUrl}")
    private String purchaseUrl;

    @Value("${project.app.resourceUrl}")
    private String resourceUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins("http://www.sikamis.com",profileUrl, financeUrl, projectUrl, purchaseUrl, resourceUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
