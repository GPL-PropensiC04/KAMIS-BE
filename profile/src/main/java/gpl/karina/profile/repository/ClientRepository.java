
package gpl.karina.profile.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gpl.karina.profile.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findByNameClientContainingIgnoreCase(String nameClient);
    Page<Client> findByNameClientContainingIgnoreCase(String nameClient, Pageable pageable);
    List<Client> findByTypeClient(Boolean typeClient);
    Page<Client> findByTypeClient(Boolean typeClient, Pageable pageable);
    List<Client> findByNameClientContainingIgnoreCaseAndTypeClient(String nameClient, Boolean typeClient);
    Page<Client> findByNameClientContainingIgnoreCaseAndTypeClient(String nameClient, Boolean typeClient, Pageable pageable);
}
