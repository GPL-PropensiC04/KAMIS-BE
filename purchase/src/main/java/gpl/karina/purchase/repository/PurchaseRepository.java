package gpl.karina.purchase.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.purchase.model.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, String> {
    long countByPurchaseSubmissionDate(Date purchaseSubmissionDate);
    // Purchase findByIdAndNotDeleted(String purchaseId);
    List<Purchase> findAllByPurchaseSupplier(UUID purchaseSupplier);
}
