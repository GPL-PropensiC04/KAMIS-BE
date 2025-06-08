package gpl.karina.profile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import gpl.karina.profile.restdto.request.AddUserReqeuestDTO;
import gpl.karina.profile.restservice.EndUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AdminInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    
    private final EndUserService endUserService;
    
    @Value("${profile.app.adminEmail}")
    private String adminEmail;
    
    @Value("${profile.app.adminUsername}")
    private String adminUsername;
    
    @Value("${profile.app.adminPassword}")
    private String adminPassword;

    public AdminInitializer(EndUserService endUserService) {
        this.endUserService = endUserService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if admin already exists
            if (endUserService.findByEmail(adminEmail) == null) {
                logger.info("Creating default admin account with email: {}", adminEmail);
                
                AddUserReqeuestDTO adminRequest = new AddUserReqeuestDTO();
                adminRequest.setEmail(adminEmail);
                adminRequest.setUsername(adminUsername);
                adminRequest.setPassword(adminPassword);
                adminRequest.setRole("admin");
                
                endUserService.addUser(adminRequest);
                logger.info("Default admin account created successfully");
            } else {
                logger.info("Admin account already exists, skipping creation");
            }
        } catch (Exception e) {
            logger.error("Failed to create default admin account: {}", e.getMessage());
        }
    }
}