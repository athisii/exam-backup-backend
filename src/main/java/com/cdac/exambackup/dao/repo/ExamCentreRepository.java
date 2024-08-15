package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.ExamCentre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface ExamCentreRepository extends JpaRepository<ExamCentre, Long> {
    ExamCentre findFirstByCode(String code);

    ExamCentre findFirstByCodeAndNameIgnoreCase(String code, String name);

    Page<ExamCentre> findByCodeOrNameIgnoreCase(String code, String name, Pageable pageable);

    Page<ExamCentre> findByNameIgnoreCase(String name, Pageable pageable);

    Page<ExamCentre> findByRegionId(Long regionId, Pageable pageable);

    @Query("SELECT ec FROM ExamCentre ec WHERE (LOWER(ec.code) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(ec.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND ec.region.id = :regionId")
    Page<ExamCentre> findByRegionIdAndQueryString(Long regionId, String query, Pageable pageable);

    @Query("SELECT ec FROM ExamCentre ec WHERE LOWER(ec.code) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(ec.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ExamCentre> findByQuery(String query, Pageable pageable);


    Page<ExamCentre> findByRegionIdAndCodeOrNameIgnoreCase(Long regionId, String code, String name, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndCode(Long regionId, String code, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndNameIgnoreCase(Long regionId, String name, Pageable pageable);
}
