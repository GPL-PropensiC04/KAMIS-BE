package gpl.karina.asset;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.javafaker.Faker;
import gpl.karina.asset.model.Asset;
import gpl.karina.asset.repository.AssetDb;

import java.util.Date;
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
                
                // Generate plate number format B1234XYZ
                String platNomor = "B" + (1000 + random.nextInt(9000)) + 
                                  String.valueOf((char)('A' + random.nextInt(26))) +
                                  String.valueOf((char)('A' + random.nextInt(26))) +
                                  String.valueOf((char)('A' + random.nextInt(26)));
                asset.setPlatNomor(platNomor);
                
                // Set asset name to a random vehicle
                asset.setNama(faker.commerce().productName());
                
                // Set jenis aset randomly
                String[] jenisAset = {"Mobil", "Motor", "Truk", "Bus", "Alat Berat"};
                asset.setJenisAset(jenisAset[random.nextInt(jenisAset.length)]);
                
                // Set status randomly
                String[] statusOptions = {"Aktif", "Tidak Aktif", "Perbaikan", "Dijual"};
                asset.setStatus(statusOptions[random.nextInt(statusOptions.length)]);
                
                // Generate acquisition date (within the last 3 years)
                asset.setTanggalPerolehan(faker.date().past(1095, TimeUnit.DAYS));
                
                // Generate acquisition value (between 50,000,000 and 500,000,000)
                asset.setNilaiPerolehan(random.nextInt() * 450000000 + 50000000);
                
                // Generate description
                asset.setDeskripsi(faker.lorem().paragraph(1));
                
                // Set asset maintenance status randomly
                // String[] maintenanceStatus = {"BAIK", "PERLU_PEMELIHARAAN", "DALAM_PEMELIHARAAN"};
                // asset.setAssetMaintenance(maintenanceStatus[random.nextInt(maintenanceStatus.length)]);
                
                // Set isDeleted to false for all dummy data
                asset.setIsDeleted(false);
                
                // Save the asset to database
                assetDb.save(asset);
                
                System.out.println("Generated dummy asset: " + asset.getNama() + " (" + asset.getPlatNomor() + ")");
            }
        };
    }
}