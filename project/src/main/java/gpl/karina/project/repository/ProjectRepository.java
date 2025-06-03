package gpl.karina.project.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gpl.karina.project.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
        List<Project> findAll();

        /**
         * Finds projects based on a set of filter criteria, including nominal ranges
         * for total income.
         * The id and projectClientId fields are explicitly cast to string
         * before applying the LOWER function to prevent "lower(bytea)" errors.
         */
        
        @Query(value = "SELECT p.*, " +
                        "d.alamat_pengambilan, d.jumlah_phl_yang_dipekerjakan, d.biaya_phl, d.total_pengeluaran " +
                        "FROM project p " +
                        "LEFT JOIN distribusi d ON p.id = d.id " +
                        
                        "WHERE ( (:idSearch IS NULL OR LOWER(p.id::text) LIKE LOWER(CONCAT('%', :idSearch, '%'))) OR " 
                        +"      (:projectName IS NULL OR LOWER(p.nama_proyek) LIKE LOWER(CONCAT('%', :projectName, '%'))) ) AND "
                        +"      (:projectStatus IS NULL OR p.status_proyek::text = :projectStatus) AND " 
                        +"      (:projectTypeBoolean IS NULL OR p.tipe_proyek = :projectTypeBoolean) AND " 
                        +"      (:projectClientId IS NULL OR LOWER(p.id_klien::text) LIKE LOWER(CONCAT('%', :projectClientId, '%'))) AND "
                        +"      (CAST(:startDate AS TIMESTAMP) IS NULL OR p.tanggal_mulai_proyek >= CAST(:startDate AS TIMESTAMP)) AND "
                        +"      (CAST(:endDate AS TIMESTAMP) IS NULL OR p.tanggal_selesai_proyek <= CAST(:endDate AS TIMESTAMP)) AND "
                        +"      (CAST(:startNominal AS BIGINT) IS NULL OR p.total_pemasukkan >= CAST(:startNominal AS BIGINT)) AND "
                        +"      (CAST(:endNominal AS BIGINT) IS NULL OR p.total_pemasukkan <= CAST(:endNominal AS BIGINT)) " 
                        +"      ORDER BY p.tanggal_mulai_proyek DESC" , nativeQuery = true)
        List<Project> findProjectsWithFilters(
                        @Param("idSearch") String idSearch,
                        @Param("projectName") String projectName,
                        @Param("projectStatus") String projectStatus,
                        @Param("projectTypeBoolean") Boolean projectTypeBoolean,
                        @Param("projectClientId") String projectClientId,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("startNominal") Long startNominal,
                        @Param("endNominal") Long endNominal);

        @Query(value = "SELECT p.*, " +
                        "d.alamat_pengambilan, d.jumlah_phl_yang_dipekerjakan, d.biaya_phl, d.total_pengeluaran " +
                        "FROM project p " +
                        "LEFT JOIN distribusi d ON p.id = d.id " +
                        
                        "WHERE ( (:idSearch IS NULL OR LOWER(p.id::text) LIKE LOWER(CONCAT('%', :idSearch, '%'))) OR " 
                        +"      (:projectName IS NULL OR LOWER(p.nama_proyek) LIKE LOWER(CONCAT('%', :projectName, '%'))) ) AND "
                        +"      (:projectStatus IS NULL OR p.status_proyek::text = :projectStatus) AND " 
                        +"      (:projectTypeBoolean IS NULL OR p.tipe_proyek = :projectTypeBoolean) AND " 
                        +"      (:projectClientId IS NULL OR LOWER(p.id_klien::text) LIKE LOWER(CONCAT('%', :projectClientId, '%'))) AND "
                        +"      (CAST(:startDate AS TIMESTAMP) IS NULL OR p.tanggal_mulai_proyek >= CAST(:startDate AS TIMESTAMP)) AND "
                        +"      (CAST(:endDate AS TIMESTAMP) IS NULL OR p.tanggal_selesai_proyek <= CAST(:endDate AS TIMESTAMP)) AND "
                        +"      (CAST(:startNominal AS BIGINT) IS NULL OR p.total_pemasukkan >= CAST(:startNominal AS BIGINT)) AND "
                        +"      (CAST(:endNominal AS BIGINT) IS NULL OR p.total_pemasukkan <= CAST(:endNominal AS BIGINT)) " 
                        +"      ORDER BY p.tanggal_mulai_proyek DESC", nativeQuery = true)
        Page<Project> findAllProjectsWithFiltersPage(
                        Pageable pageable,
                        @Param("idSearch") String idSearch,
                        @Param("projectName") String projectName,
                        @Param("projectStatus") String projectStatus,
                        @Param("projectTypeBoolean") Boolean projectTypeBoolean,
                        @Param("projectClientId") String projectClientId,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("startNominal") Long startNominal,
                        @Param("endNominal") Long endNominal);

        @Query("SELECT COUNT(p) FROM Project p WHERE FUNCTION('DATE', p.createdDate) = FUNCTION('DATE', :date)")
        Long countProjectsCreatedOn(@Param("date") Date date);

        List<Project> findByProjectStartDate(Date today);

        List<Project> findBycreatedDate(Date today);

        @Query("SELECT COUNT(p) FROM Project p WHERE p.createdDate BETWEEN :start AND :end AND p.projectType = :type AND p.projectStatus IN :statuses")
        Long countByCreatedDateBetweenAndProjectTypeAndProjectStatusIn(
                        @Param("start") Date start,
                        @Param("end") Date end,
                        @Param("type") Boolean type,
                        @Param("statuses") List<Integer> statuses);

        // =====WEEKLY=====
        @Query("SELECT p.createdDate, COUNT(p) " +
                        "FROM Project p " +
                        "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
                        "AND p.projectType = :projectType " +
                        "AND p.projectStatus IN :statuses " +
                        "GROUP BY p.createdDate")
        List<Object[]> getDailyProjectCountInStatus(@Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("statuses") List<Integer> statuses,
                        @Param("projectType") Boolean projectType);

        @Query("SELECT p.createdDate, COUNT(p) " +
                        "FROM Project p " +
                        "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
                        "AND p.projectType = :projectType " +
                        "AND p.projectStatus NOT IN :statuses " +
                        "GROUP BY p.createdDate")
        List<Object[]> getDailyProjectCountExcludeStatus(@Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("statuses") List<Integer> statuses,
                        @Param("projectType") Boolean projectType);

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
