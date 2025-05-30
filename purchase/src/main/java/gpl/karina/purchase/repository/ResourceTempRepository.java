package gpl.karina.purchase.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.purchase.model.ResourceTemp;

@Repository
public interface ResourceTempRepository extends JpaRepository<ResourceTemp, UUID> {
    
}
