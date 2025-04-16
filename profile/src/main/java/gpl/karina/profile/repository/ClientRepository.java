package gpl.karina.profile.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.profile.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    
}
