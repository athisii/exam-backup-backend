package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamDate;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    List<Exam> findByExamCentreId(Long examCentreId);

    @Query("SELECT e.id FROM Exam e WHERE e.examCentre.id = :examCentreId")
    List<Long> findIdsByExamCentreId(Long examCentreId);

    boolean existsByExamDateIdAndDeletedFalse(Long examDateId);

    long deleteByExamCentreId(Long examCentreId);

    @Modifying
    @Query("DELETE FROM Exam e WHERE e.id IN :ids")
    void deleteByIdIn(List<Long> ids);
}
