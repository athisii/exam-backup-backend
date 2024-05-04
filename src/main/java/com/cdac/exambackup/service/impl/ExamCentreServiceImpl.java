package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.service.ExamCentreService;
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
public class ExamCentreServiceImpl extends AbstractBaseService<ExamCentre, Long> implements ExamCentreService {
    @Autowired
    ExamCentreDao examCentreDao;

    public ExamCentreServiceImpl(BaseDao<ExamCentre, Long> baseDao) {
        super(baseDao);
    }
}
