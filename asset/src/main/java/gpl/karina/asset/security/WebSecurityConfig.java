package gpl.karina.asset.security;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gpl.karina.asset.security.jwt.JwtTokenFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test").permitAll()
                        // Allow GET requests to all assets (list) for all authorized roles
                        .requestMatchers(HttpMethod.GET, "/api/asset/all")
                        .hasAnyAuthority("Admin", "Direksi", "Finance", "Operasional")
                        // Allow GET requests for specific asset detail for all authorized roles
                        .requestMatchers(HttpMethod.GET, "/api/asset/{platNomor}")
                        .hasAnyAuthority("Admin", "Direksi", "Finance", "Operasional")
                        // Restrict DELETE operations to only Admin and Operasional roles
                        .requestMatchers(HttpMethod.DELETE, "/api/asset/**")
                        .hasAnyAuthority("Operasional", "Admin")
                        // Restrict PUT operations to only Admin and Operasional roles
                        .requestMatchers(HttpMethod.PUT, "/api/asset/**")
                        .hasAnyAuthority("Operasional", "Admin")
                        // Restrict POST operations to only Admin and Operasional roles
                        .requestMatchers(HttpMethod.POST, "/api/asset/**")
                        .hasAnyAuthority("Operasional", "Admin")
                        // Restrict PATCH operations to only Admin and Operasional roles
                        .requestMatchers(HttpMethod.PATCH, "/api/asset/**")
                        .hasAnyAuthority("Operasional", "Admin")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}