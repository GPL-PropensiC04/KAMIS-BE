package gpl.karina.purchase.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.purchase.model.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, String> {
    long countByPurchaseSubmissionDate(LocalDate purchaseSubmissionDate);
}
