package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamDao;
import com.cdac.exambackup.dao.repo.ExamRepository;
import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamDate;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class ExamDaoImpl extends AbstractBaseDao<Exam, Long> implements ExamDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    ExamRepository examRepository;

    @Override
    public JpaRepository<Exam, Long> getRepository() {
        return this.examRepository;
    }

    @Override
    public Class<Exam> getEntityClass() {
        return Exam.class;
    }

    @Override
    public Exam findByExamCentreAndExamDate(ExamCentre examCentre, ExamDate examDate) {
        return this.examRepository.findFirstByExamCentreAndExamDateAndDeletedFalse(examCentre, examDate);
    }

    @Override
    public Exam findByExamCentreIdAndExamDateId(Long examCentreId, Long examDateId) {
        return this.examRepository.findFirstByExamCentreIdAndExamDateIdAndDeletedFalse(examCentreId, examDateId);
    }

    @Override
    public Page<Exam> getByExamCentreId(Long examCentreId, Pageable pageable) {
        try {
            return this.examRepository.findByExamCentreIdAndDeletedFalse(examCentreId, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public List<Exam> findByExamCentreId(Long examCentreId) {
        return examRepository.findByExamCentreIdAndDeletedFalse(examCentreId);
    }

    @Override
    public List<Long> findIdsByExamCentreId(Long examCentreId) {
        return examRepository.findIdsByExamCentreId(examCentreId);
    }

    @Override
    public void deleteByExamCentre(ExamCentre examCentre) {
        examRepository.deleteByExamCentre(examCentre);
    }
}
