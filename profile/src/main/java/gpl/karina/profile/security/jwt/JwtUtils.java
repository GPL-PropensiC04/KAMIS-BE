package gpl.karina.profile.security.jwt;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
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
import jakarta.annotation.PostConstruct;
 
@Component
public class JwtUtils {

    private final UserDetailsServiceImpl userDetailsService;

    public JwtUtils(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
 
    @Value("${profile.app.jwtSecret}")
    private String jwtSecretKeyString;

    @Value("${profile.app.jwtPublicKey}")
    private String jwtPublicKeyString;
 
    @Value("${profile.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    // Initialize the keys after the bean is constructed
    @PostConstruct
    public void init() {
        try {
            logger.debug("Initializing priv RSA keys: {}", jwtSecretKeyString);
            logger.debug("Initializing Pub RSA keys: {}", jwtPublicKeyString);
            // Decode and create PrivateKey
            byte[] privateKeyBytes = Base64.getDecoder().decode(jwtSecretKeyString);
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.privateKey = kf.generatePrivate(privateSpec);

            // Decode and create PublicKey
            byte[] publicKeyBytes = Base64.getDecoder().decode(jwtPublicKeyString);
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = kf.generatePublic(publicSpec);
        } catch (Exception e) {
            throw new RuntimeException("Could not load RSA keys", e);
        }
    }

    public String generateJwtToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        logger.debug("Generating token for user: {} with role: {}", username, role);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // Include role if needed
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
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
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        return false;
    }
}