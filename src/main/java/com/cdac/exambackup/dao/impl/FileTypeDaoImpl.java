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

import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    @Override
    public void softDelete(FileType entity) {
        if (entity != null) {
            entity.setDeleted(true);
            entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
            entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());
            fileTypeRepository.save(entity);
        }
    }

    @Override
    public void softDelete(Collection<FileType> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(entity -> {
                entity.setDeleted(true);
                entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
                entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());
            });
            fileTypeRepository.saveAll(entities);
        }
    }

    @Override
    public List<FileType> findByCodeOrName(String code, String name) {
        return fileTypeRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name);
    }
}
