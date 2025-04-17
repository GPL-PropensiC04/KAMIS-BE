package gpl.karina.profile.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.profile.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findAll();
}
