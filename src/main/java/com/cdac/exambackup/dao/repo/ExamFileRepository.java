package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface ExamFileRepository extends JpaRepository<ExamFile, Long> {
    List<ExamFile> findByExamCentreAndDeletedFalse(ExamCentre examCentre);

    List<ExamFile> findByExamCentreIdAndExamDateIdAndSlotIdAndDeletedFalse(Long examCentreId, Long examDateId, Long slotId);

    ExamFile findFirstByExamCentreAndExamDateAndSlotAndFileTypeAndDeletedFalse(ExamCentre examCentre, ExamDate examDate, Slot slot, FileType fileType);
}
