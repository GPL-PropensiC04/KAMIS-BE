package gpl.karina.project.security;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gpl.karina.project.security.jwt.JwtTokenFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String ROLE_ADMIN = "Admin";
    private static final String ROLE_OPERASIONAL = "Operasional";
    private static final String ROLE_FINANCE = "Finance";
    private static final String ROLE_DIREKSI = "Direksi";

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
                        .requestMatchers("/api/project/add").hasAnyAuthority( ROLE_OPERASIONAL)
                        .requestMatchers("/api/project/chart/**").hasAnyAuthority( ROLE_OPERASIONAL)
                        .requestMatchers("/api/project/update/**").hasAnyAuthority( ROLE_OPERASIONAL, ROLE_DIREKSI)
                        .requestMatchers("/api/project/update-status/**").hasAnyAuthority(ROLE_OPERASIONAL)
                        .requestMatchers("/api/project/update-payment/**").hasAnyAuthority(ROLE_FINANCE)
                        .requestMatchers("/api/project/**")
                        .hasAnyAuthority(ROLE_ADMIN, ROLE_FINANCE, ROLE_OPERASIONAL, ROLE_DIREKSI)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}