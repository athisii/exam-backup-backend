package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface ExamCentreRepository extends JpaRepository<ExamCentre, Long> {
    ExamCentre findFirstByCodeIgnoreCase(String code);

    ExamCentre findFirstByCodeAndName(String code, String name);

    ExamCentre findFirstByName(String name);

    Page<ExamCentre> findByRegion(Region region, Pageable pageable);
}
