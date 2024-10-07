package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.dao.repo.ExamSlotRepository;
import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.Slot;
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
public class ExamSlotDaoImpl extends AbstractBaseDao<ExamSlot, Long> implements ExamSlotDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    ExamSlotRepository examSlotRepository;

    @Override
    public JpaRepository<ExamSlot, Long> getRepository() {
        return this.examSlotRepository;
    }

    @Override
    public Class<ExamSlot> getEntityClass() {
        return ExamSlot.class;
    }

    @Override
    public ExamSlot findByExamAndSlot(Exam exam, Slot slot) {
        return this.examSlotRepository.findFirstByExamAndSlotAndDeletedFalse(exam, slot);
    }

    @Override
    public Page<ExamSlot> findByExamId(Long examId, Pageable pageable) {
        try {
            return this.examSlotRepository.findByExamIdAndDeletedFalse(examId, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public List<ExamSlot> findByExamId(Long examId) {
        return this.examSlotRepository.findByExamIdAndDeletedFalse(examId);
    }

    @Override
    public boolean checkIfSlotIdExist(Long slotId) {
        return this.examSlotRepository.existsBySlotIdAndDeletedFalse(slotId);
    }

    @Override
    public long countByExamId(Long examId) {
        return this.examSlotRepository.countByExamIdAndDeletedFalse(examId);
    }

    @Override
    public void deleteByExamIdIn(List<Long> examIds) {
        this.examSlotRepository.deleteByExamIdIn(examIds);
    }
}
