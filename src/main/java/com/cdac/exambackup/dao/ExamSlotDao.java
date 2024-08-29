package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamSlotDao extends BaseDao<ExamSlot, Long> {
    ExamSlot findByExamAndSlot(Exam exam, Slot slot);

    Page<ExamSlot> findByExamId(Long examId, Pageable pageable);
}
