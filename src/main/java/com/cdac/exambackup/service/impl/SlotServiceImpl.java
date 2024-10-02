package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamDao;
import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.dao.SlotDao;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.Slot;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.SlotService;
import com.cdac.exambackup.util.NullAndBlankUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class SlotServiceImpl extends AbstractBaseService<Slot, Long> implements SlotService {
    final SlotDao slotDao;
    final ExamSlotDao examSlotDao;
    final ExamDao examDao;

    public SlotServiceImpl(BaseDao<Slot, Long> baseDao, SlotDao slotDao, ExamSlotDao examSlotDao, ExamDao examDao) {
        super(baseDao);
        this.slotDao = slotDao;
        this.examSlotDao = examSlotDao;
        this.examDao = examDao;
    }

    @Transactional
    @Override
    public Slot save(Slot slotDto) {
        // new record entry
        if (slotDto.getId() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(slotDto.getCode(), slotDto.getName())) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank");
            }
            if (slotDto.getStartTime() == null || slotDto.getEndTime() == null) {
                throw new InvalidReqPayloadException("'Start Time' and 'End Time' cannot be null");
            }
            if (slotDto.getStartTime().isAfter(slotDto.getEndTime()) || slotDto.getStartTime().equals(slotDto.getEndTime())) {
                throw new InvalidReqPayloadException("'Start Time' cannot be greater or equal to 'End Time'");
            }
            // try adding a new record (more performant)
            // if violation constraint exception is thrown then duplicate exists.
            try {
                slotDto.setCode(slotDto.getCode().toUpperCase().trim());
                slotDto.setName(slotDto.getName().toUpperCase().trim());
                return slotDao.save(slotDto);
            } catch (Exception ex) {
                log.info("Error occurred while creating a new slot: {}", ex.getMessage());
                throw new InvalidReqPayloadException("Same 'name', 'code', or same 'start time - end time' already exists.");
            }
        }
        // else updating existing record.

        // if both values are invalid throw error; one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(slotDto.getCode(), slotDto.getName()) && NullAndBlankUtil.isAllNull(slotDto.getStartTime(), slotDto.getEndTime())) {
            throw new InvalidReqPayloadException("One value must be not null or blank");
        }

        Slot daoSlot = slotDao.findById(slotDto.getId());
        if (daoSlot == null) {
            throw new EntityNotFoundException("Slot with id: " + slotDto.getId() + " not found.");
        }

        if (slotDto.getCode() != null) {
            if (slotDto.getCode().isBlank()) {
                throw new InvalidReqPayloadException("code cannot be blank.");
            }
            daoSlot.setCode(slotDto.getCode().trim().toUpperCase());
        }
        if (slotDto.getName() != null) {
            if (slotDto.getName().isBlank()) {
                throw new InvalidReqPayloadException("name cannot be blank.");
            }
            daoSlot.setName(slotDto.getName().trim().toUpperCase());
        }
        if (slotDto.getStartTime() != null) {
            daoSlot.setStartTime(slotDto.getStartTime());
        }
        if (slotDto.getEndTime() != null) {
            daoSlot.setEndTime(slotDto.getEndTime());
        }
        if (daoSlot.getStartTime().isAfter(daoSlot.getEndTime()) || daoSlot.getStartTime().equals(daoSlot.getEndTime())) {
            throw new InvalidReqPayloadException("'Start Time' cannot be greater or equal to 'End Time'");
        }
        // since the transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore, catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return slotDao.save(daoSlot);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<Slot>> getAllByPage(Pageable pageable) {
        Page<Slot> page = slotDao.getAllByPage(pageable);
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), page.getContent());
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<Slot>> getByExamCentreIdAndExamDateId(Long examCentreId, Long examDateId, Pageable pageable) {
        Exam daoExam = examDao.findByExamCentreIdAndExamDateId(examCentreId, examDateId);
        if (daoExam == null) {
            throw new InvalidReqPayloadException("Exam not found where exam date id: " + examDateId + " and  exam centre id: " + examCentreId);
        }
        Page<ExamSlot> page = examSlotDao.findByExamId(daoExam.getId(), pageable);
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), page.getContent().stream().map(ExamSlot::getSlot).toList());
    }
}
