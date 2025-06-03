package gpl.karina.asset.repository;
import gpl.karina.asset.model.Asset;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    // Pagination without filters
    Page<Asset> findByIsDeletedFalse(Pageable pageable);

    // Single parameter filters
    Page<Asset> findByNamaContainingIgnoreCaseAndIsDeletedFalse(String nama, Pageable pageable);
    Page<Asset> findByJenisAsetContainingIgnoreCaseAndIsDeletedFalse(String jenisAset, Pageable pageable);
    Page<Asset> findByStatusContainingIgnoreCaseAndIsDeletedFalse(String status, Pageable pageable);
    
    // Two parameter filters
    Page<Asset> findByNamaContainingIgnoreCaseAndJenisAsetContainingIgnoreCaseAndIsDeletedFalse(
        String nama, String jenisAset, Pageable pageable);
    Page<Asset> findByNamaContainingIgnoreCaseAndStatusContainingIgnoreCaseAndIsDeletedFalse(
        String nama, String status, Pageable pageable);
    Page<Asset> findByJenisAsetContainingIgnoreCaseAndStatusContainingIgnoreCaseAndIsDeletedFalse(
        String jenisAset, String status, Pageable pageable);
    
    // Three parameter filter
    Page<Asset> findByNamaContainingIgnoreCaseAndJenisAsetContainingIgnoreCaseAndStatusContainingIgnoreCaseAndIsDeletedFalse(
        String nama, String jenisAset, String status, Pageable pageable);
}
