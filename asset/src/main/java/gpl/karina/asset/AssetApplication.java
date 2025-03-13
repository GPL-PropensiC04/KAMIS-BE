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
    
}