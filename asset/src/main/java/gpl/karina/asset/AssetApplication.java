package gpl.karina.asset;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.javafaker.Faker;
import gpl.karina.asset.model.Asset;
import gpl.karina.asset.repository.AssetDb;

import java.util.Date;
import java.util.UUID;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class AssetApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetApplication.class, args);
    }
    
    @Bean
    CommandLineRunner initDatabase(AssetDb assetDb) {
        return args -> {
            Faker faker = new Faker();
            Random random = new Random();
            
            // Generate 10 dummy assets
            for (int i = 0; i < 10; i++) {
                Asset asset = new Asset();
                
                // Generate a unique ID using UUID
                asset.setId(UUID.randomUUID().toString());
                
                // Set asset name to a random product
                asset.setNama(faker.commerce().productName());
                
                // Generate description
                asset.setDeskripsi(faker.lorem().paragraph(1));
                
                // Generate acquisition date (within the last 3 years)
                asset.setTanggalPerolehan(faker.date().past(1095, TimeUnit.DAYS));
                
                // Generate acquisition value (between 1,000,000 and 50,000,000)
                asset.setNilaiPerolehan(random.nextFloat() * 49000000 + 1000000);
                
                // Set asset maintenance status randomly
                String[] maintenanceStatus = {"GOOD", "NEEDS_MAINTENANCE", "UNDER_MAINTENANCE"};
                asset.setAssetMaintenance(maintenanceStatus[random.nextInt(maintenanceStatus.length)]);
                
                // Set isDeleted to false for all dummy data
                asset.setIsDeleted(false);
                
                // Save the asset to database
                assetDb.save(asset);
                
                System.out.println("Generated dummy asset: " + asset.getNama());
            }
        };
    }
}