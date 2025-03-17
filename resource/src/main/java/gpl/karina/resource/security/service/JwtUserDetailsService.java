package gpl.karina.resource.security.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gpl.karina.resource.security.jwt.JwtUtils;


@Service
public class JwtUserDetailsService implements UserDetailsService {
    
    private final JwtUtils jwtUtils;
    
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
        authorities.add(new SimpleGrantedAuthority(role));
        
        // Note: password is null since we're authenticating via token
        return new User(username, "", authorities);
    }
}