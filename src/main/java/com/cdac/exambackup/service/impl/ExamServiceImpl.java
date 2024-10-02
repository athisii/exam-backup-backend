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
    static final String NOT_FOUND = " not found";

    final ExamDao examDao;
    final ExamCentreDao examCentreDao;
    final ExamDateDao examDateDao;

    public ExamServiceImpl(BaseDao<Exam, Long> baseDao, ExamDao examDao, ExamCentreDao examCentreDao, ExamDateDao examDateDao) {
        super(baseDao);
        this.examDao = examDao;
        this.examCentreDao = examCentreDao;
        this.examDateDao = examDateDao;
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
                throw new EntityNotFoundException("ExamCentre with id: " + examReqDto.examCentreId() + NOT_FOUND);
            }
            ExamDate daoExamDate = examDateDao.findById(examReqDto.examDateId());
            if (daoExamDate == null) {
                throw new EntityNotFoundException("ExamDate with id: " + examReqDto.examDateId() + NOT_FOUND);
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
            throw new EntityNotFoundException("Exam with id: " + examReqDto.id() + NOT_FOUND);
        }

        if (examReqDto.examDateId() != null) {
            ExamCentre daoExamCentre = examCentreDao.findById(examReqDto.examCentreId());
            if (daoExamCentre == null) {
                throw new EntityNotFoundException("ExamCentre with id: " + examReqDto.examCentreId() + NOT_FOUND);
            }
            daoExam.setExamCentre(daoExamCentre);
        }
        if (examReqDto.examCentreId() != null) {
            ExamDate daoExamDate = examDateDao.findById(examReqDto.examDateId());
            if (daoExamDate == null) {
                throw new EntityNotFoundException("ExamDate with id: " + examReqDto.examDateId() + NOT_FOUND);
            }
            daoExam.setExamDate(daoExamDate);
        }
        // since the transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore, catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return examDao.save(daoExam);
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
