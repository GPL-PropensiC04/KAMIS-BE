package gpl.karina.finance.report.config;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class CorsConfig {

    @Value("${finance.report.app.profileUrl}")
    private String profileUrl;

    @Value("${finance.report.app.financeUrl}")
    private String financeUrl;

    @Value("${finance.report.app.projectUrl}")
    private String projectUrl;

    @Value("${finance.report.app.purchaseUrl}")
    private String purchaseUrl;

    @Value("${finance.report.app.resourceUrl}")
    private String resourceUrl;

    @Value("${finance.report.app.frontendUrl}")
    private String frontendUrl;

    @Value("${finance.report.app.assetUrl}")
    private String assetUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins(frontendUrl, profileUrl, financeUrl, projectUrl, purchaseUrl, resourceUrl, assetUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
