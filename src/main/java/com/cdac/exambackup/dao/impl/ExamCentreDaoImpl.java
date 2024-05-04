package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.dao.repo.ExamCentreRepository;
import com.cdac.exambackup.entity.ExamCentre;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamCentreDaoImpl extends AbstractBaseDao<ExamCentre, Long> implements ExamCentreDao {
    @Autowired
    ExamCentreRepository examCentreRepository;

    @Override
    public JpaRepository<ExamCentre, Long> getRepository() {
        return this.examCentreRepository;
    }

    @Override
    public Class<ExamCentre> getEntityClass() {
        return ExamCentre.class;
    }
}
