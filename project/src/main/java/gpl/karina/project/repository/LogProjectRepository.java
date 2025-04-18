package gpl.karina.project.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gpl.karina.project.model.LogProject;

@Repository
public interface LogProjectRepository extends JpaRepository<LogProject, UUID> {
    
}
