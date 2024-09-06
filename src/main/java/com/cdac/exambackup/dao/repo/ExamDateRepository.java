package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.ExamDate;
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
public interface ExamDateRepository extends JpaRepository<ExamDate, Long> {
    @Query("SELECT ed FROM ExamDate ed WHERE ed.deleted = false ORDER BY ed.date ASC")
    Page<ExamDate> findByDeletedFalse(Pageable pageable);
}
