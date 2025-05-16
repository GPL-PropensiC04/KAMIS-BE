package gpl.karina.finance.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.finance.report.model.Lapkeu;

@Repository
public interface LapkeuRepository extends JpaRepository<Lapkeu, Long> {
}
