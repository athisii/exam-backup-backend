package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.Slot;
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
public interface ExamSlotRepository extends JpaRepository<ExamSlot, Long> {
    ExamSlot findFirstByExamAndSlotAndDeletedFalse(Exam exam, Slot slot);

    Page<ExamSlot> findByExamIdAndDeletedFalse(Long examId, Pageable pageable);

    boolean existsBySlotIdAndDeletedFalse(Long slotId);

    long countByExamIdAndDeletedFalse(Long examId);

    @Modifying
    @Query("DELETE FROM ExamSlot e WHERE e.exam.id IN :examIds")
    void deleteByExamIdIn(List<Long> examIds);
}
