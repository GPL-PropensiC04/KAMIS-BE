package gpl.karina.finance.report.security.jwt;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${finance.report.app.jwtPublicKey}")
    private String jwtPublicKeyString;

    private PublicKey publicKey;

    // Initialize the keys after the bean is constructed
    @PostConstruct
    public void init() {
        try {
            logger.debug("Initializing JWT public key");
            // Decode and create PublicKey
            byte[] publicKeyBytes = Base64.getDecoder().decode(jwtPublicKeyString);
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.publicKey = kf.generatePublic(publicSpec);
        } catch (Exception e) {
            throw new RuntimeException("Could not load RSA public key", e);
        }
    }

    public String getUserNameFromJwtToken(String token) {
        try {
            JwtParserBuilder parserBuilder = Jwts.parser();
            parserBuilder.setSigningKey(publicKey);
            JwtParser jwtParser = parserBuilder.build();
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
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
            JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey).build();
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
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
            JwtParser parser = Jwts.parser().setSigningKey(publicKey).build();
            parser.parseClaimsJws(authToken);
            logger.debug("JWT token is valid");
            return true;
        } catch (JwtException e) {
            logger.error("JWT token validation error: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}