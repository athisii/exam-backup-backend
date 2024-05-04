package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamSlotDao;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.service.ExamSlotService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
