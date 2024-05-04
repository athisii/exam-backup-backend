package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.ExamFileDao;
import com.cdac.exambackup.entity.ExamFile;
import com.cdac.exambackup.service.ExamFileService;
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
public class ExamFileServiceImpl extends AbstractBaseService<ExamFile, Long> implements ExamFileService {
    @Autowired
    ExamFileDao examFileDao;

    public ExamFileServiceImpl(BaseDao<ExamFile, Long> baseDao) {
        super(baseDao);
    }
}
