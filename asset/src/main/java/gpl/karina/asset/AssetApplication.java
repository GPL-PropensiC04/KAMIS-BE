package gpl.karina.asset;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import gpl.karina.asset.model.Asset;
import gpl.karina.asset.repository.AssetDb;

import java.util.Date;
import java.util.UUID;

@SpringBootApplication
public class AssetApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner loadSampleData(AssetDb assetDb) {
        return args -> {
            // Check if there's already data in the database
            if (assetDb.count() > 0) {
                System.out.println("Data already exists, skipping sample data generation");
                return;
            }
            
            System.out.println("Generating sample asset data...");
            
            // Create 5 sample assets
            createSampleAsset(
                assetDb,
                "Laptop Dell XPS 15",
                "Laptop premium untuk kebutuhan desain dan development dengan spesifikasi high-end",
                new Date(System.currentTimeMillis() - (365 * 24 * 60 * 60 * 1000L)), // 1 year ago
                15000000f,
                "Baik"
            );
            
            createSampleAsset(
                assetDb,
                "Printer HP LaserJet Pro",
                "Printer laser untuk kebutuhan kantor dengan kecepatan cetak tinggi",
                new Date(System.currentTimeMillis() - (730 * 24 * 60 * 60 * 1000L)), // 2 years ago
                3500000f,
                "Memerlukan Service"
            );
            
            createSampleAsset(
                assetDb,
                "AC Daikin 1.5 PK",
                "AC ruangan untuk menjaga suhu server tetap dingin",
                new Date(System.currentTimeMillis() - (180 * 24 * 60 * 60 * 1000L)), // 6 months ago
                4800000f,
                "Baik"
            );
            
            createSampleAsset(
                assetDb,
                "Mobil Operasional Toyota Avanza",
                "Kendaraan operasional perusahaan untuk keperluan transportasi karyawan dan barang",
                new Date(System.currentTimeMillis() - (1095 * 24 * 60 * 60 * 1000L)), // 3 years ago
                210000000f,
                "Dalam Perbaikan"
            );
            
            createSampleAsset(
                assetDb,
                "Proyektor Epson EB-U05",
                "Proyektor untuk meeting room dengan resolusi Full HD",
                new Date(System.currentTimeMillis() - (548 * 24 * 60 * 60 * 1000L)), // 1.5 years ago
                7200000f,
                "Rusak"
            );
            
            System.out.println("Sample data generation completed. Created 5 assets.");
        };
    }
    
    private void createSampleAsset(AssetDb assetDb, String nama, String deskripsi, 
                                Date tanggalPerolehan, Float nilaiPerolehan, String assetMaintenance) {
        Asset asset = new Asset();
        asset.setId(UUID.randomUUID().toString());
        asset.setNama(nama);
        asset.setDeskripsi(deskripsi);
        asset.setTanggalPerolehan(tanggalPerolehan);
        asset.setNilaiPerolehan(nilaiPerolehan);
        asset.setAssetMaintenance(assetMaintenance);
        assetDb.save(asset);
    }
}