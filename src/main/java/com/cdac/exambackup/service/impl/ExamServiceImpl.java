package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.dao.ExamDao;
import com.cdac.exambackup.dao.ExamDateDao;
import com.cdac.exambackup.dto.ExamReqDto;
import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamDate;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.ExamService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ExamServiceImpl extends AbstractBaseService<Exam, Long> implements ExamService {
    @Autowired
    ExamDao examDao;
    @Autowired
    ExamCentreDao examCentreDao;

    @Autowired
    ExamDateDao examDateDao;


    public ExamServiceImpl(BaseDao<Exam, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public Exam save(ExamReqDto examReqDto) {
        // new entry
        if (examReqDto.id() == null) {
            if (examReqDto.examDateId() == null || examReqDto.examCentreId() == null) {
                throw new InvalidReqPayloadException("Please provide all the required ids.");
            }
            ExamCentre daoExamCentre = examCentreDao.findById(examReqDto.examCentreId());
            if (daoExamCentre == null) {
                throw new EntityNotFoundException("ExamCentre with id: " + examReqDto.examCentreId() + " not found.");
            }
            ExamDate daoExamDate = examDateDao.findById(examReqDto.examDateId());
            if (daoExamDate == null) {
                throw new EntityNotFoundException("ExamDate with id: " + examReqDto.examDateId() + " not found.");
            }
            Exam exam = examDao.findByExamCentreAndExamDate(daoExamCentre, daoExamDate);
            if (exam != null) {
                throw new InvalidReqPayloadException("Exam already exists for given examCentreId and examDateId.");
            }
            exam = new Exam();
            exam.setExamDate(daoExamDate);
            exam.setExamCentre(daoExamCentre);
            return examDao.save(exam);
        }
        // else update existing entry

        if (examReqDto.examDateId() == null && examReqDto.examCentreId() == null) {
            throw new InvalidReqPayloadException("Please provide all the required ids.");
        }

        Exam daoExam = examDao.findById(examReqDto.id());
        if (daoExam == null) {
            throw new EntityNotFoundException("Exam with id: " + examReqDto.id() + " not found.");
        }

        if (examReqDto.examDateId() != null) {
            ExamCentre daoExamCentre = examCentreDao.findById(examReqDto.examCentreId());
            if (daoExamCentre == null) {
                throw new EntityNotFoundException("ExamCentre with id: " + examReqDto.examCentreId() + " not found.");
            }
            daoExam.setExamCentre(daoExamCentre);
        }
        if (examReqDto.examCentreId() != null) {
            ExamDate daoExamDate = examDateDao.findById(examReqDto.examDateId());
            if (daoExamDate == null) {
                throw new EntityNotFoundException("ExamDate with id: " + examReqDto.examDateId() + " not found.");
            }
            daoExam.setExamDate(daoExamDate);
        }
        try {
            return examDao.save(daoExam);
        } catch (Exception ex) {
            log.warn("Error: {}", ex.getMessage());
            throw new InvalidReqPayloadException("Same exam already exists.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Exam getByExamCentreIdAndExamDateId(Long examCentreId, Long examDateId) {
        Exam daoExam = examDao.findByExamCentreIdAndExamDateId(examCentreId, examDateId);
        if (daoExam == null) {
            throw new InvalidReqPayloadException("Exam not found where exam date id: " + examDateId + " and  exam centre id: " + examCentreId);
        }
        return daoExam;
    }
}
