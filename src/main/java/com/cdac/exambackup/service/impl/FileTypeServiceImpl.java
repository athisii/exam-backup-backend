package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.FileTypeDao;
import com.cdac.exambackup.entity.FileType;
import com.cdac.exambackup.service.FileTypeService;
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
public class FileTypeServiceImpl extends AbstractBaseService<FileType, Long> implements FileTypeService {
    @Autowired
    FileTypeDao fileTypeDao;

    public FileTypeServiceImpl(BaseDao<FileType, Long> baseDao) {
        super(baseDao);
    }
}
