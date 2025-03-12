package gpl.karina.asset.repository;
import gpl.karina.asset.model.Asset;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetDb extends JpaRepository<Asset, String> {
    List<Asset> findAll();
    List<Asset> findByNamaContaining(String nama);
}
