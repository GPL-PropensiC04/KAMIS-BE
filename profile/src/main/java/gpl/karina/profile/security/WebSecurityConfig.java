package gpl.karina.profile.security;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
    
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import gpl.karina.profile.security.jwt.JwtTokenFilter;

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
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/profile/add").hasAnyAuthority("Admin")
                        .requestMatchers("/api/profile/all").hasAnyAuthority("Admin")
                        .requestMatchers("/api/profile/**").hasAnyAuthority("Admin", "Direksi", "Finance", "Operasional")
                        .requestMatchers("/api/client/all").hasAnyAuthority("Operasional","Direksi")
                        .requestMatchers("/api/client/add").hasAnyAuthority("Operasional")
                        .requestMatchers("/api/supplier/add").hasAnyAuthority("Operasional", "Admin")
                        .requestMatchers("/api/supplier/update").hasAnyAuthority("Operasional", "Admin")
                        .requestMatchers("/api/supplier/add-purchase").hasAnyAuthority("Operasional", "Admin")
                        .requestMatchers("/api/supplier/**").hasAnyAuthority("Admin", "Direksi", "Finance", "Operasional")
                        .anyRequest().permitAll())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}