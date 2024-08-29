package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.*;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamFileDao extends BaseDao<ExamFile, Long> {
    ExamFile findByExamCentreAndExamDateAndSlotAndFileType(ExamCentre examCentre, ExamDate examDate, Slot slot, FileType fileType);

    List<ExamFile> findByExamCentreAndExamDateAndSlot(ExamCentre examCentre, ExamDate examDate, Slot slot);

    List<ExamFile> findByExamCentre(ExamCentre example);
}
