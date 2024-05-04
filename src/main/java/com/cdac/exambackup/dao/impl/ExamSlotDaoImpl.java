package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.dao.repo.ExamSlotRepository;
import com.cdac.exambackup.entity.ExamSlot;
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
public class ExamSlotDaoImpl extends AbstractBaseDao<ExamSlot, Long> implements ExamSlotDao {
    @Autowired
    ExamSlotRepository examSlotRepository;

    @Override
    public JpaRepository<ExamSlot, Long> getRepository() {
        return this.examSlotRepository;
    }

    @Override
    public Class<ExamSlot> getEntityClass() {
        return ExamSlot.class;
    }
}
