package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamFile;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.FileType;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamFileDao extends BaseDao<ExamFile, Long> {
    List<ExamFile> findByExamCentreAndExamSlotAndFileType(ExamCentre examCentre, ExamSlot examSlot, FileType fileType);

    List<ExamFile> findByExamCentreAndExamSlot(ExamCentre examCentre, ExamSlot examSlot);

    List<ExamFile> findByExamCentre(ExamCentre example);
}
