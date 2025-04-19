package gpl.karina.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import gpl.karina.resource.model.Resource;
import jakarta.persistence.LockModeType;

public interface ResourceRepository extends JpaRepository<Resource, Long>{
    Resource findByResourceName(String resourceName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Resource r WHERE r.id = :id")
    Optional<Resource> findByIdWithPessimisticLock(@Param("id") Long id);
}
