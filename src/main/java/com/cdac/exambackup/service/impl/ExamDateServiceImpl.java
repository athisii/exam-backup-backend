package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.dao.ExamDateDao;
import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.dto.ExamDateReqDto;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamDate;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.ExamDateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamDateServiceImpl extends AbstractBaseService<ExamDate, Long> implements ExamDateService {
    @Autowired
    ExamDateDao examDateDao;
    @Autowired
    ExamCentreDao examCentreDao;
    @Autowired
    ExamSlotDao examSlotDao;


    public ExamDateServiceImpl(BaseDao<ExamDate, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public ExamDate save(ExamDateReqDto examDateReqDto) {
        // new entry
        if (examDateReqDto.id() == null) {
            ExamCentre daoExamCentre = examCentreDao.findById(examDateReqDto.examCentreId());
            if (daoExamCentre == null) {
                throw new EntityNotFoundException("ExamCentre with id: " + examDateReqDto.examCentreId() + " not found.");
            }
            if (examDateReqDto.examDate() == null) {
                throw new GenericException("ExamDate must not be null.");
            }
            Set<ExamSlot> examSlots = new HashSet<>();
            examDateReqDto.examSlotIds().forEach(examSlotId -> {
                ExamSlot daoExamSlot = examSlotDao.findById(examSlotId);
                if (daoExamSlot == null) {
                    throw new EntityNotFoundException("ExamSlot with id: " + examSlotId + " not found.");
                }
                examSlots.add(daoExamSlot);
            });
            ExamDate examDate = new ExamDate();
            examDate.setExamCentre(daoExamCentre);
            if (!examSlots.isEmpty()) {
                examDate.setExamSlots(examSlots);
            }
            examDate.setExamDate(examDateReqDto.examDate());
            return examDateDao.save(examDate);
        }
        //else update existing entry
        ExamDate daoExamDate = examDateDao.findById(examDateReqDto.id());
        if (daoExamDate == null) {
            throw new EntityNotFoundException("ExamDate with id: " + examDateReqDto.id() + " not found.");
        }

        if (examDateReqDto.examDate() != null) {
            daoExamDate.setExamDate(examDateReqDto.examDate());
        }

        if (examDateReqDto.examCentreId() != null) {
            ExamCentre daoExamCentre = examCentreDao.findById(examDateReqDto.examCentreId());
            if (daoExamCentre == null) {
                throw new EntityNotFoundException("ExamCentre with id: " + examDateReqDto.examCentreId() + " not found.");
            }
            daoExamDate.setExamCentre(daoExamCentre);
        }

        if (examDateReqDto.examSlotIds() != null) {
            Set<ExamSlot> examSlots = new HashSet<>();
            examDateReqDto.examSlotIds().forEach(examSlotId -> {
                ExamSlot daoExamSlot = examSlotDao.findById(examSlotId);
                if (daoExamSlot == null) {
                    throw new EntityNotFoundException("ExamSlot with id: " + examSlotId + " not found.");
                }
                examSlots.add(daoExamSlot);
            });
            if (!examSlots.isEmpty()) {
                daoExamDate.setExamSlots(examSlots);
            }
        }
        return examDateDao.save(daoExamDate);
    }
}
