package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamDateDao;
import com.cdac.exambackup.dao.repo.ExamDateRepository;
import com.cdac.exambackup.dao.repo.ExamRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamDateDaoImpl extends AbstractBaseDao<ExamDate, Long> implements ExamDateDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";
    static final LocalDate twentyTwenty = LocalDate.of(2020, 12, 30);

    @Autowired
    ExamDateRepository examDateRepository;
    @Autowired
    ExamRepository examRepository;

    @Override
    public JpaRepository<ExamDate, Long> getRepository() {
        return this.examDateRepository;
    }

    @Override
    public Class<ExamDate> getEntityClass() {
        return ExamDate.class;
    }

    private void markDeletedAndSubtractDaysById(ExamDate examDate) {
        if (examRepository.existsByExamDateIdAndDeletedFalse(examDate.getId())) {
            throw new InvalidReqPayloadException("Exam Date: " + examDate.getDate() + " is associated with some exams. Cannot delete it.");
        }
        examDate.setDeleted(true);
        // user should be allowed to add same date after deleted
        examDate.setDate(twentyTwenty.minusDays(examDate.getId()));
    }

    @Transactional
    @Override
    public void softDelete(ExamDate examDate) {
        markDeletedAndSubtractDaysById(examDate);
        examDateRepository.save(examDate);
    }

    @Transactional
    @Override
    public void softDelete(Collection<ExamDate> examDates) {
        if (examDates != null && !examDates.isEmpty()) {
            examDates.forEach(this::markDeletedAndSubtractDaysById);
            examDateRepository.saveAll(examDates);
        }
    }

    @Override
    public Page<ExamDate> getAllByPage(Pageable pageable) {
        try {
            return this.examDateRepository.findByDeletedFalse(pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }
}
