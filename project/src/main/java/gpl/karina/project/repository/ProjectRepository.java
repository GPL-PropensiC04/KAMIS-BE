package gpl.karina.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import gpl.karina.project.model.Project;

import java.util.Date;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findAll();

    List<Project> findByProjectStartDate(Date today);

    List<Project> findBycreatedDate(Date today);
    
}
