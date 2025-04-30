package gpl.karina.profile.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.profile.model.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    List<Supplier> findAll();
    boolean existsByNameSupplier(String nameSupplier);
    boolean existsByNoTelpSupplier(String noTelpSupplier);
    boolean existsByEmailSupplier(String emailSupplier);
    boolean existsByCompanySupplier(String companySupplier);
    boolean existsByNoTelpSupplierAndIdNot(String noTelpSupplier, UUID id);
    boolean existsByEmailSupplierAndIdNot(String emailSupplier, UUID id);
    boolean existsByNameSupplierAndIdNot(String nameSupplier, UUID id);

}
