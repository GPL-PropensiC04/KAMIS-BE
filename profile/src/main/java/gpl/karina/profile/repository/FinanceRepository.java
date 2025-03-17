package gpl.karina.profile.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.profile.model.Finance;

@Repository
public interface FinanceRepository extends JpaRepository<Finance, UUID> {
}