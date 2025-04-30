package gpl.karina.asset.repository;
import gpl.karina.asset.model.Asset;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetDb extends JpaRepository<Asset, String> {
    List<Asset> findAll();
    List<Asset> findByNamaContaining(String nama);
    
    @Query("SELECT a FROM Asset a WHERE a.isDeleted = false")
    List<Asset> findAllActive();
    
    @Query("SELECT a FROM Asset a WHERE a.platNomor = :id AND a.isDeleted = false")
    Asset findByIdAndNotDeleted(@Param("id") String id);
    
    @Modifying
    @Query("UPDATE Asset a SET a.isDeleted = true WHERE a.platNomor = :id")
    void softDeleteById(@Param("id") String id);
    
    List<Asset> findByIdSupplierAndIsDeletedFalse(UUID supplierId);
}
