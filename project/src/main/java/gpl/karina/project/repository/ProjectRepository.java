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
    
    // ===== MONTHLY =====
    @Query("SELECT TO_CHAR(p.createdDate, 'YYYY-MM'), COUNT(p) " +
        "FROM Project p " +
        "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
        "AND p.projectType = :projectType " +
        "AND p.projectStatus IN :statuses " +
        "GROUP BY TO_CHAR(p.createdDate, 'YYYY-MM') ORDER BY 1")
    List<Object[]> getMonthlyProjectCountInStatus(@Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                @Param("statuses") List<Integer> statuses,
                                                @Param("projectType") Boolean projectType);

    @Query("SELECT TO_CHAR(p.createdDate, 'YYYY-MM'), COUNT(p) " +
        "FROM Project p " +
        "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
        "AND p.projectType = :projectType " +
        "AND p.projectStatus NOT IN :statuses " +
        "GROUP BY TO_CHAR(p.createdDate, 'YYYY-MM') ORDER BY 1")
    List<Object[]> getMonthlyProjectCountExcludeStatus(@Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate,
                                                    @Param("statuses") List<Integer> statuses,
                                                    @Param("projectType") Boolean projectType);


    // ===== QUARTERLY =====
    @Query("SELECT CONCAT(EXTRACT(YEAR FROM p.createdDate), '-Q', EXTRACT(QUARTER FROM p.createdDate)), COUNT(p) " +
        "FROM Project p " +
        "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
        "AND p.projectType = :projectType " +
        "AND p.projectStatus IN :statuses " +
        "GROUP BY EXTRACT(YEAR FROM p.createdDate), EXTRACT(QUARTER FROM p.createdDate) ORDER BY 1")
    List<Object[]> getQuarterlyProjectCountInStatus(@Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate,
                                                    @Param("statuses") List<Integer> statuses,
                                                    @Param("projectType") Boolean projectType);

    @Query("SELECT CONCAT(EXTRACT(YEAR FROM p.createdDate), '-Q', EXTRACT(QUARTER FROM p.createdDate)), COUNT(p) " +
        "FROM Project p " +
        "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
        "AND p.projectType = :projectType " +
        "AND p.projectStatus NOT IN :statuses " +
        "GROUP BY EXTRACT(YEAR FROM p.createdDate), EXTRACT(QUARTER FROM p.createdDate) ORDER BY 1")
    List<Object[]> getQuarterlyProjectCountExcludeStatus(@Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate,
                                                        @Param("statuses") List<Integer> statuses,
                                                        @Param("projectType") Boolean projectType);


    // ===== YEARLY =====
    @Query("SELECT CAST(EXTRACT(YEAR FROM p.createdDate) AS string), COUNT(p) " +
        "FROM Project p " +
        "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
        "AND p.projectType = :projectType " +
        "AND p.projectStatus IN :statuses " +
        "GROUP BY EXTRACT(YEAR FROM p.createdDate) ORDER BY 1")
    List<Object[]> getYearlyProjectCountInStatus(@Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                @Param("statuses") List<Integer> statuses,
                                                @Param("projectType") Boolean projectType);

    @Query("SELECT CAST(EXTRACT(YEAR FROM p.createdDate) AS string), COUNT(p) " +
        "FROM Project p " +
        "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
        "AND p.projectType = :projectType " +
        "AND p.projectStatus NOT IN :statuses " +
        "GROUP BY EXTRACT(YEAR FROM p.createdDate) ORDER BY 1")
    List<Object[]> getYearlyProjectCountExcludeStatus(@Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate,
                                                    @Param("statuses") List<Integer> statuses,
                                                    @Param("projectType") Boolean projectType);

}
