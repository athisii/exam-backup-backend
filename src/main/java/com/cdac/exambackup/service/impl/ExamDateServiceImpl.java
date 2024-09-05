package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.dao.ExamDao;
import com.cdac.exambackup.dao.ExamDateDao;
import com.cdac.exambackup.dto.ExamDateReqDto;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamDate;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.ExamDateService;
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
public class ExamDateServiceImpl extends AbstractBaseService<ExamDate, Long> implements ExamDateService {
    @Autowired
    ExamDateDao examDateDao;
    @Autowired
    ExamCentreDao examCentreDao;

    @Autowired
    ExamDao examDao;


    public ExamDateServiceImpl(BaseDao<ExamDate, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public ExamDate save(ExamDateReqDto examDateReqDto) {
        // new entry
        if (examDateReqDto.id() == null) {
            if (examDateReqDto.date() == null) {
                throw new InvalidReqPayloadException("Please provide date.");
            }
            try {
                return examDateDao.save(new ExamDate(examDateReqDto.date()));
            } catch (Exception e) {
                throw new InvalidReqPayloadException("Same date already exists.");
            }
        }
        //else update existing entry
        if (examDateReqDto.date() == null) {
            throw new InvalidReqPayloadException("Please provide date.");
        }
        ExamDate daoExamDate = examDateDao.findById(examDateReqDto.id());
        if (daoExamDate == null) {
            throw new EntityNotFoundException("ExamDate with id: " + examDateReqDto.id() + " not found.");
        }
        daoExamDate.setDate(examDateReqDto.date());

        // since transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return examDateDao.save(daoExamDate);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<ExamDate>> getByExamCentreId(Long examCentreId, Pageable pageable) {
        Page<Exam> page = examDao.getByExamCentreId(examCentreId, pageable);
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), page.getContent().stream().map(Exam::getExamDate).toList());
    }
}
