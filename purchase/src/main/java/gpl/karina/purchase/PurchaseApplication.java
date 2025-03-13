package gpl.karina.purchase;

import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.javafaker.Faker;

import gpl.karina.purchase.model.AssetTemp;
import gpl.karina.purchase.repository.AssetTempRepository;

import jakarta.transaction.Transactional;

@SpringBootApplication
public class PurchaseApplication {
    private static boolean isSeeded = false;
    private final Faker faker = new Faker(new Locale("in-ID"));

    public static void main(String[] args) {
        SpringApplication.run(PurchaseApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner run(AssetTempRepository assetTempRepository) {
        return args -> {
            if (isSeeded) {
                System.out.println("Data has already been seeded. Skipping generation.");
                return;
            }

            System.out.println("Generating fake construction vehicle assets...");

            String[] vehicleTypes = {
                "Excavator", "Bulldozer", "Backhoe Loader", "Dump Truck", "Crane",
                "Concrete Mixer", "Forklift", "Road Roller", "Grader", "Trencher"
            };

            for (int i = 0; i < 10; i++) {
                AssetTemp asset = new AssetTemp();
                asset.setAssetName(vehicleTypes[i % vehicleTypes.length] + " " + faker.bothify("##??")); // Unique identifier
                asset.setAssetDescription("A " + vehicleTypes[i % vehicleTypes.length] + " used for construction work.");
                asset.setAssetType("Construction Vehicle");
                asset.setAssetPrice(faker.number().numberBetween(50_000_000, 500_000_000)); // IDR Price range

                assetTempRepository.save(asset); // Save to database
            }

            isSeeded = true; // Mark as seeded
        };
    }
}
