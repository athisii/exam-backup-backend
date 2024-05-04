package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.FileTypeDao;
import com.cdac.exambackup.dao.repo.FileTypeRepository;
import com.cdac.exambackup.entity.FileType;
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
public class FileTypeDaoImpl extends AbstractBaseDao<FileType, Long> implements FileTypeDao {
    @Autowired
    FileTypeRepository fileTypeRepository;

    @Override
    public JpaRepository<FileType, Long> getRepository() {
        return this.fileTypeRepository;
    }

    @Override
    public Class<FileType> getEntityClass() {
        return FileType.class;
    }
}
