package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamDao;
import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.dao.SlotDao;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.Slot;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.SlotService;
import com.cdac.exambackup.util.NullAndBlankUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    SlotDao slotDao;

    @Autowired
    ExamSlotDao examSlotDao;

    @Autowired
    ExamDao examDao;

    public SlotServiceImpl(BaseDao<Slot, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public Slot save(Slot slotDto) {
        /*
             if id not present in dto:
                  add new record after passing the constraint check.
             else:
                  if entity exist in table for passed id:
                       update only {code} and {name} after passing the constraint check. // other fields have separate API.
                  else:
                       throw exception.
         */

        // new record entry
        if (slotDto.getId() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(slotDto.getCode(), slotDto.getName())) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or empty");
            }
            if (slotDto.getStartTime() == null || slotDto.getEndTime() == null) {
                throw new InvalidReqPayloadException("'Start Time' and 'End Time' cannot be null");
            }
            if (slotDto.getStartTime().isAfter(slotDto.getEndTime()) || slotDto.getStartTime().equals(slotDto.getEndTime())) {
                throw new InvalidReqPayloadException("'Start Time' cannot be greater or equal to 'End Time'");
            }
            // brute force add to for performance
            try {
                slotDto.setCode(slotDto.getCode().toUpperCase());
                slotDto.setName(slotDto.getName().toUpperCase());
                return slotDao.save(slotDto);
            } catch (Exception ex) {
                log.info("Error saving slot.", ex);
                throw new InvalidReqPayloadException("Same 'name', 'code', 'start time' and/or 'end time' already exists");
            }
        }
        // else updating existing record.

        Slot daoSlot = slotDao.findById(slotDto.getId());
        if (daoSlot == null) {
            throw new EntityNotFoundException("ExamSlot with id: " + slotDto.getId() + " not found.");
        }
        if (Boolean.FALSE.equals(daoSlot.getActive())) {
            throw new EntityNotFoundException("ExamSlot with id: " + daoSlot.getId() + " is not active. Must activate first.");
        }

        // if both values are invalid, one should be valid
        if ((slotDto.getCode() == null && slotDto.getName() == null) || (slotDto.getCode() != null && slotDto.getCode().isBlank() && slotDto.getName() != null && slotDto.getName().isBlank())) {
            throw new GenericException("Both 'code' and 'name' cannot be null or empty");
        }

        List<Slot> daoOtherSlots;
        if (slotDto.getName() == null) {
            daoOtherSlots = slotDao.findByCodeOrName(slotDto.getCode(), null);
        } else {
            daoOtherSlots = slotDao.findByCodeOrName(slotDto.getCode(), slotDto.getName().trim());
        }
        // check if it's the different object
        if ((daoOtherSlots != null && daoOtherSlots.size() > 1) || daoOtherSlots != null && !daoOtherSlots.isEmpty() && !daoSlot.getId().equals(daoOtherSlots.getFirst().getId())) {
            throw new InvalidReqPayloadException("Same 'code' or 'name' already exists");
        }

        if (slotDto.getCode() != null) {
            if (slotDto.getCode().isBlank()) {
                throw new InvalidReqPayloadException("code cannot be empty.");
            }
            daoSlot.setCode(slotDto.getCode());
        }
        if (slotDto.getName() != null) {
            if (slotDto.getName().isBlank()) {
                throw new InvalidReqPayloadException("name cannot be empty.");
            }
            daoSlot.setName(slotDto.getName().trim().toUpperCase());
        }
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
