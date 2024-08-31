package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.dao.repo.ExamSlotRepository;
import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.Slot;
import com.cdac.exambackup.exception.GenericException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamSlotDaoImpl extends AbstractBaseDao<ExamSlot, Long> implements ExamSlotDao {

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
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ex.getMessage());
        }
    }

    @Override
    public boolean checkIfSlotIdExist(Long slotId) {
        return this.examSlotRepository.existsBySlotIdAndDeletedFalse(slotId);
    }
}
