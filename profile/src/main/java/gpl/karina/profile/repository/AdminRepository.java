package gpl.karina.profile.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.profile.model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
    
}
