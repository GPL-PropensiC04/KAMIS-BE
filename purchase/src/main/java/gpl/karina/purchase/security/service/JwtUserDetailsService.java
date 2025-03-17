package gpl.karina.purchase.security.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gpl.karina.purchase.security.jwt.JwtUtils;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);
    public JwtUserDetailsService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
        // Extract information from the token instead of a database
        if (!jwtUtils.validateJwtToken(token)) {
            throw new UsernameNotFoundException("Invalid token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        String role = jwtUtils.getRoleFromToken(token);

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority( role));
        logger.info("authorities: {}", authorities);
        // Note: password is null since we're authenticating via token
        return new User(username, "", authorities);
    }
}