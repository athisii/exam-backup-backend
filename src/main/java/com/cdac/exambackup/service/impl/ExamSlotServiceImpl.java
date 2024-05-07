package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.ExamSlotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ExamSlotServiceImpl extends AbstractBaseService<ExamSlot, Long> implements ExamSlotService {
    @Autowired
    ExamSlotDao examSlotDao;

    public ExamSlotServiceImpl(BaseDao<ExamSlot, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public ExamSlot save(ExamSlot examSlotDto) {
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
        if (examSlotDto.getId() == null) {
            // if both values are invalid, throw exception
            if (examSlotDto.getCode() == null || examSlotDto.getCode() <= 0 || examSlotDto.getName() == null || examSlotDto.getName().isBlank()) {
                throw new GenericException("Both 'code' and 'name' cannot be null or empty");
            }
            List<ExamSlot> daoExamSlots = examSlotDao.findByCodeOrName(examSlotDto.getCode(), examSlotDto.getName().trim());
            if (!daoExamSlots.isEmpty()) {
                throw new GenericException("Same 'code' or 'name' already exists");
            }
            // now remove the unnecessary fields if present or create new object.
            ExamSlot examSlot = new ExamSlot();
            examSlot.setCode(examSlotDto.getCode());
            examSlot.setName(examSlotDto.getName().trim().toUpperCase());
            return examSlotDao.save(examSlot);
        }
        // else updating existing record.

        ExamSlot daoExamSlot = examSlotDao.findById(examSlotDto.getId());
        if (daoExamSlot == null) {
            throw new EntityNotFoundException("ExamSlot with id: " + examSlotDto.getId() + " not found.");
        }

        // if both values are invalid, one should be valid
        if ((examSlotDto.getCode() == null && examSlotDto.getName() == null) || (examSlotDto.getCode() != null && examSlotDto.getCode() <= 0 && examSlotDto.getName() != null && examSlotDto.getName().isBlank())) {
            throw new GenericException("Both 'code' and 'name' cannot be null or empty");
        }

        List<ExamSlot> daoOtherExamSlots;
        if (examSlotDto.getName() == null) {
            daoOtherExamSlots = examSlotDao.findByCodeOrName(examSlotDto.getCode(), null);
        } else {
            daoOtherExamSlots = examSlotDao.findByCodeOrName(examSlotDto.getCode(), examSlotDto.getName().trim());
        }
        // check if it's the different object
        if ((daoOtherExamSlots != null && daoOtherExamSlots.size() > 1) || daoOtherExamSlots != null && !daoOtherExamSlots.isEmpty() && daoExamSlot != daoOtherExamSlots.getFirst()) {
            throw new GenericException("Same 'code' or 'name' already exists");
        }

        if (examSlotDto.getCode() != null) {
            if (examSlotDto.getCode() <= 0) {
                throw new GenericException("code must be greater than 0");
            }
            daoExamSlot.setCode(examSlotDto.getCode());
        }
        if (examSlotDto.getName() != null) {
            if (examSlotDto.getName().isBlank()) {
                throw new GenericException("name cannot be empty.");
            }
            daoExamSlot.setName(examSlotDto.getName().trim().toUpperCase());
        }
        return examSlotDao.save(daoExamSlot);
    }
}
