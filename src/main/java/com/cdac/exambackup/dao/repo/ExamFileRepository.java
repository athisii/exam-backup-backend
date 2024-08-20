package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamFile;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.FileType;
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
    List<ExamFile> findByExamCentreAndExamSlotAndFileType(ExamCentre examCentre, ExamSlot examSlot, FileType fileType);
    List<ExamFile> findByExamCentreAndExamSlot(ExamCentre examCentre, ExamSlot examSlot);
    List<ExamFile> findByExamCentre(ExamCentre examCentre);
}
