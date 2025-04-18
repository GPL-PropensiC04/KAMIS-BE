package gpl.karina.profile.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.profile.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findAll();
    List<Client> findByNameClientContainingIgnoreCaseAndTypeClient(String nameClient, boolean typeClient);
    List<Client> findByNameClientContainingIgnoreCase(String nameClient);
    List<Client> findByTypeClient(boolean typeClient);
}
