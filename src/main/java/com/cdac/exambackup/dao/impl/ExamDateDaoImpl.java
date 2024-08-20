package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamDateDao;
import com.cdac.exambackup.dao.repo.ExamDateRepository;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamDate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ExamDateDaoImpl extends AbstractBaseDao<ExamDate, Long> implements ExamDateDao {
    @Autowired
    ExamDateRepository examDateRepository;

    @Override
    public JpaRepository<ExamDate, Long> getRepository() {
        return this.examDateRepository;
    }

    @Override
    public Class<ExamDate> getEntityClass() {
        return ExamDate.class;
    }

    @Override
    public List<ExamDate> findByExamCentre(ExamCentre examCentre) {
        return examDateRepository.findByExamCentre(examCentre);
    }
}
