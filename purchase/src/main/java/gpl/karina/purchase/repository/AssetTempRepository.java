package gpl.karina.purchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.purchase.model.AssetTemp;

@Repository
public interface AssetTempRepository extends JpaRepository<AssetTemp, Long> {
    
}
