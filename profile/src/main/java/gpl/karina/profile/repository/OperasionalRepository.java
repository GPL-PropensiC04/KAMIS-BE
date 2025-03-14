package gpl.karina.profile.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.profile.model.Operasional;

@Repository
public interface OperasionalRepository extends JpaRepository<Operasional, UUID> {
}