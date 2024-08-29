package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamDate;
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
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Exam findFirstByExamCentreAndExamDateAndDeletedFalse(ExamCentre examCentre, ExamDate examDate);

    Exam findFirstByExamCentreIdAndExamDateIdAndDeletedFalse(Long examCentreId, Long examDateId);

    Page<Exam> findByExamCentreIdAndDeletedFalse(Long examCentreId, Pageable pageable);
}
