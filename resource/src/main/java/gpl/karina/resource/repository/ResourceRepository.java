package gpl.karina.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import gpl.karina.resource.model.Resource;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;


public interface ResourceRepository extends JpaRepository<Resource, Long>{
    Resource findByResourceName(String resourceName);

    @Query("SELECT r FROM Resource r JOIN r.supplierId s WHERE s = :supplierId")
    List<Resource> findBySupplierId(@Param("supplierId") UUID supplierId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Resource r WHERE r.id = :id")
    Optional<Resource> findByIdWithPessimisticLock(@Param("id") Long id);
    
    List<Resource> findByResourceStockLessThanEqual(Integer stock);
}
