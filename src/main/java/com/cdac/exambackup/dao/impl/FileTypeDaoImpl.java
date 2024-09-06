package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.FileTypeDao;
import com.cdac.exambackup.dao.repo.ExamFileRepository;
import com.cdac.exambackup.dao.repo.FileTypeRepository;
import com.cdac.exambackup.entity.FileType;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    FileTypeRepository fileTypeRepository;

    @Autowired
    ExamFileRepository examFileRepository;

    @Override
    public JpaRepository<FileType, Long> getRepository() {
        return this.fileTypeRepository;
    }

    @Override
    public Class<FileType> getEntityClass() {
        return FileType.class;
    }

    private void markDeletedAndAddSuffix(FileType fileType) {
        if (examFileRepository.existsByFileTypeIdAndDeletedFalse(fileType.getId())) {
            throw new InvalidReqPayloadException("FileType code: " + fileType.getCode() + " is associated with some exam files. Cannot delete it.");
        }
        fileType.setDeleted(true);
        // user should be allowed to add same name after deleted
        // add suffix to avoid unique constraint violation for code
        fileType.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + fileType.getCode());
        // add suffix to avoid unique constraint violation for name
        fileType.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + fileType.getName());
    }

    @Override
    public void softDelete(FileType fileType) {
        markDeletedAndAddSuffix(fileType);
        fileTypeRepository.save(fileType);
    }

    @Override
    public void softDelete(Collection<FileType> fileTypes) {
        if (fileTypes != null && !fileTypes.isEmpty()) {
            fileTypes.forEach(this::markDeletedAndAddSuffix);
            fileTypeRepository.saveAll(fileTypes);
        }
    }

    @Override
    public List<FileType> findByCodeOrName(String code, String name) {
        return fileTypeRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name);
    }

    @Override
    public long countNonDeleted() {
        return fileTypeRepository.countByDeletedFalse();
    }

    @Override
    public Page<FileType> getAllByPage(Pageable pageable) {
        try {
            return this.fileTypeRepository.findByDeletedFalse(pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }
}
