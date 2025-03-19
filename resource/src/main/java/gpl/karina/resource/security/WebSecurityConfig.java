package gpl.karina.resource.security;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gpl.karina.resource.security.jwt.JwtTokenFilter;

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
                        .requestMatchers("/api/resource/add").hasAnyAuthority("Admin", "Operasional")
                        .requestMatchers("/api/resource/viewall").hasAnyAuthority("Direksi", "Finance", "Operasional", "Admin")
                        .requestMatchers("/api/resource/update/**").hasAnyAuthority("Operasional", "Admin")
                        .requestMatchers("/api/resource/**").hasAnyAuthority("Direksi", "Finance", "Operasional", "Admin")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}