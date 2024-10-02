package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamDao;
import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.dao.SlotDao;
import com.cdac.exambackup.dto.ExamSlotReqDto;
import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.Slot;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.ExamSlotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamSlotServiceImpl extends AbstractBaseService<ExamSlot, Long> implements ExamSlotService {
    final ExamDao examDao;
    final SlotDao slotDao;
    final ExamSlotDao examSlotDao;

    public ExamSlotServiceImpl(BaseDao<ExamSlot, Long> baseDao, ExamDao examDao, SlotDao slotDao, ExamSlotDao examSlotDao) {
        super(baseDao);
        this.examDao = examDao;
        this.slotDao = slotDao;
        this.examSlotDao = examSlotDao;
    }

    @Transactional
    @Override
    public ExamSlot save(ExamSlotReqDto examSlotReqDto) {
        // new entry
        if (examSlotReqDto.id() == null) {
            if (examSlotReqDto.examId() == null || examSlotReqDto.slotId() == null) {
                throw new InvalidReqPayloadException("Please provide all the required ids.");
            }
            Slot daoSlot = slotDao.findById(examSlotReqDto.slotId());
            if (daoSlot == null) {
                throw new EntityNotFoundException("Slot with id: " + examSlotReqDto.slotId() + " not found.");
            }
            Exam daoExam = examDao.findById(examSlotReqDto.examId());
            if (daoExam == null) {
                throw new EntityNotFoundException("Exam with id: " + examSlotReqDto.examId() + " not found.");
            }
            ExamSlot daoExamSlot = examSlotDao.findByExamAndSlot(daoExam, daoSlot);
            if (daoExamSlot != null) {
                throw new InvalidReqPayloadException("ExamSlot already exists for given examId and slotId.");
            }
            daoExamSlot = new ExamSlot();
            daoExamSlot.setExam(daoExam);
            daoExamSlot.setSlot(daoSlot);
            return examSlotDao.save(daoExamSlot);
        }
        // else update existing entry

        if (examSlotReqDto.examId() == null && examSlotReqDto.slotId() == null) {
            throw new InvalidReqPayloadException("Please provide all the required ids.");
        }

        ExamSlot daoExamSlot = examSlotDao.findById(examSlotReqDto.id());
        if (daoExamSlot == null) {
            throw new EntityNotFoundException("ExamSlot with id: " + examSlotReqDto.id() + " not found.");
        }

        if (examSlotReqDto.slotId() != null) {
            Slot daoSlot = slotDao.findById(examSlotReqDto.slotId());
            if (daoSlot == null) {
                throw new EntityNotFoundException("Slot with id: " + examSlotReqDto.slotId() + " not found.");
            }
            daoExamSlot.setSlot(daoSlot);
        }
        if (examSlotReqDto.examId() != null) {
            Exam daoExam = examDao.findById(examSlotReqDto.examId());
            if (daoExam == null) {
                throw new EntityNotFoundException("Exam with id: " + examSlotReqDto.examId() + " not found.");
            }
            daoExamSlot.setExam(daoExam);
        }
        // since transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return examSlotDao.save(daoExamSlot);
    }
}
