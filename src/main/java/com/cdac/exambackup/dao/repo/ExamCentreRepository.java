package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.ExamCentre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface ExamCentreRepository extends JpaRepository<ExamCentre, Long> {
    ExamCentre findFirstByCodeAndDeletedFalse(String code);

    ExamCentre findFirstByCodeAndNameIgnoreCaseAndDeletedFalse(String code, String name);

    Page<ExamCentre> findByCodeOrNameIgnoreCaseAndDeletedFalse(String code, String name, Pageable pageable);

    Page<ExamCentre> findByNameIgnoreCaseAndDeletedFalse(String name, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndDeletedFalse(Long regionId, Pageable pageable);

    @Query("SELECT ec FROM ExamCentre ec WHERE (LOWER(ec.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(ec.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND ec.region.id = :regionId AND ec.deleted = false")
    Page<ExamCentre> findByRegionIdAndSearchTermAndDeletedFalse(Long regionId, String searchTerm, Pageable pageable);

    @Query("SELECT ec FROM ExamCentre ec WHERE LOWER(ec.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(ec.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND ec.deleted = false")
    Page<ExamCentre> findBySearchTermAndDeletedFalse(String searchTerm, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndCodeOrNameIgnoreCaseAndDeletedFalse(Long regionId, String code, String name, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndCodeAndDeletedFalse(Long regionId, String code, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndNameIgnoreCaseAndDeletedFalse(Long regionId, String name, Pageable pageable);

    List<ExamCentre> findByRegionIdAndDeletedFalse(Long regionId);

    boolean existsByRegionIdAndDeletedFalse(Long regionId);

    @Query("SELECT ec FROM ExamCentre ec WHERE ec.deleted = false ORDER BY CAST(ec.code AS INTEGER) ASC")
    Page<ExamCentre> findByDeletedFalse(Pageable pageable);

    List<ExamCentre> getAllByRegionIdInAndDeletedFalse(List<Long> regionIds);
}
