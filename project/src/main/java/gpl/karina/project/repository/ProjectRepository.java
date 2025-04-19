package gpl.karina.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import gpl.karina.project.model.Project;

import java.util.Date;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findAll();
    
    @Query("SELECT COUNT(p) FROM Project p WHERE FUNCTION('DATE', p.createdDate) = FUNCTION('DATE', :date)")
    Long countProjectsCreatedOn(@Param("date") Date date);
    List<Project> findByProjectStartDate(Date today);

    List<Project> findBycreatedDate(Date today);
    
}
