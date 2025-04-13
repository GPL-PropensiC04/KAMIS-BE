package gpl.karina.asset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gpl.karina.asset.model.Maintenance;

public interface MaintenanceDb extends JpaRepository<Maintenance, Long> {    
}
