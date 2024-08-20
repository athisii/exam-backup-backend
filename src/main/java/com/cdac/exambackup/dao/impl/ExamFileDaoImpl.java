package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamFileDao;
import com.cdac.exambackup.dao.repo.ExamFileRepository;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamFile;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.FileType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamFileDaoImpl extends AbstractBaseDao<ExamFile, Long> implements ExamFileDao {
    @Autowired
    ExamFileRepository examFileRepository;

    @Override
    public JpaRepository<ExamFile, Long> getRepository() {
        return examFileRepository;
    }

    @Override
    public Class<ExamFile> getEntityClass() {
        return ExamFile.class;
    }

    @Override
    public List<ExamFile> findByExamCentreAndExamSlotAndFileType(ExamCentre examCentre, ExamSlot examSlot, FileType fileType) {
        return this.examFileRepository.findByExamCentreAndExamSlotAndFileType(examCentre, examSlot, fileType);
    }

    @Override
    public List<ExamFile> findByExamCentreAndExamSlot(ExamCentre examCentre, ExamSlot examSlot) {
        return this.examFileRepository.findByExamCentreAndExamSlot(examCentre, examSlot);
    }

    @Override
    public List<ExamFile> findByExamCentre(ExamCentre examCentre) {
        return this.examFileRepository.findByExamCentre(examCentre);
    }
}
