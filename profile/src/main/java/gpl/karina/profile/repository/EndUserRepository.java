package gpl.karina.profile.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import gpl.karina.profile.model.EndUser;

@Repository
public interface EndUserRepository extends JpaRepository<EndUser, UUID> {
    Optional<EndUser> findByEmail(String email);
    Optional<EndUser> findByUsername(String username);

 // Single parameter filters
 Page<EndUser> findByEmailContainingIgnoreCase(String email, Pageable pageable);
 Page<EndUser> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
 Page<EndUser> findByUserTypeContainingIgnoreCase(String userType, Pageable pageable);
 
 // Two parameter filters
 Page<EndUser> findByEmailContainingIgnoreCaseAndUsernameContainingIgnoreCase(String email, String username, Pageable pageable);
 Page<EndUser> findByEmailContainingIgnoreCaseAndUserTypeContainingIgnoreCase(String email, String userType, Pageable pageable);
 Page<EndUser> findByUsernameContainingIgnoreCaseAndUserTypeContainingIgnoreCase(String username, String userType, Pageable pageable);
 
 // Three parameter filter
 Page<EndUser> findByEmailContainingIgnoreCaseAndUsernameContainingIgnoreCaseAndUserTypeContainingIgnoreCase(String email, String username, String userType, Pageable pageable);
}
