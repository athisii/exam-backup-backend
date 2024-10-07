package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.FileExtensionDao;
import com.cdac.exambackup.dao.repo.FileExtensionRepository;
import com.cdac.exambackup.dao.repo.FileTypeRepository;
import com.cdac.exambackup.entity.FileExtension;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class FileExtensionDaoImpl extends AbstractBaseDao<FileExtension, Long> implements FileExtensionDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    FileExtensionRepository fileExtensionRepository;

    @Autowired
    FileTypeRepository fileTypeRepository;

    @Override
    public JpaRepository<FileExtension, Long> getRepository() {
        return this.fileExtensionRepository;
    }

    @Override
    public Class<FileExtension> getEntityClass() {
        return FileExtension.class;
    }

    private void markDeletedAndAddSuffix(FileExtension fileExtension) {
        if (fileTypeRepository.existsByFileExtensionIdAndDeletedFalse(fileExtension.getId())) {
            throw new InvalidReqPayloadException("FileExtension code: " + fileExtension.getCode() + " is associated with some file types. Cannot delete it.");
        }
        fileExtension.setDeleted(true);
        // user should be allowed to add same name after deleted
        // add suffix to avoid unique constraint violation for code
        fileExtension.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + fileExtension.getCode());
        // add suffix to avoid unique constraint violation for name
        fileExtension.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + fileExtension.getName());
    }

    @Transactional
    @Override
    public void softDelete(FileExtension fileExtension) {
        markDeletedAndAddSuffix(fileExtension);
        fileExtensionRepository.save(fileExtension);
    }

    @Transactional
    @Override
    public void softDelete(Collection<FileExtension> fileExtensions) {
        if (fileExtensions != null && !fileExtensions.isEmpty()) {
            fileExtensions.forEach(this::markDeletedAndAddSuffix);
            fileExtensionRepository.saveAll(fileExtensions);
        }
    }

    @Override
    public List<FileExtension> findByCodeOrName(String code, String name) {
        return fileExtensionRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name);
    }

    @Override
    public long countNonDeleted() {
        return fileExtensionRepository.countByDeletedFalse();
    }

    @Override
    public Page<FileExtension> getAllByPage(Pageable pageable) {
        try {
            return this.fileExtensionRepository.findByDeletedFalse(pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }
}
