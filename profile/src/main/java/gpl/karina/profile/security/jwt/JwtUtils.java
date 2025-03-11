package gpl.karina.profile.security.jwt;

import java.util.Date;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import gpl.karina.profile.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
 
@Component
public class JwtUtils {

    private final UserDetailsServiceImpl userDetailsService;

    public JwtUtils(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
 
    @Value("${profile.app.jwtSecret}")
    private String jwtSecret;
 
    @Value("${profile.app.jwtExpirationMs}")
    private int jwtExpirationMs;
 
    public String generateJwtToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        logger.debug("Generating token for user: {} with role: {}", username, role);

        return Jwts.builder()
            .subject(username)
            .claim("role", role)  // Make sure role is being set correctly
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
            .compact();
    }
 
    public String getUserNameFromJwtToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String username = claims.getSubject();
            logger.debug("Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Error extracting username from token", e);
            throw e;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String role = claims.get("role", String.class);
            logger.debug("Extracted role from token: {}", role);
            return role;
        } catch (Exception e) {
            logger.error("Error extracting role from token", e);
            throw e;
        }
    }
 
    public boolean validateJwtToken(String authToken) {
        try {
            JwtParser parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
            parser.parseSignedClaims(authToken);
            logger.debug("JWT token is valid");
            return true;
        } catch(SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch(IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch(MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch(ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch(UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        return false;
    }
}