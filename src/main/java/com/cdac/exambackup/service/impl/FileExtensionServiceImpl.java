package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.FileExtensionDao;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.FileExtension;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.FileExtensionService;
import com.cdac.exambackup.util.NullAndBlankUtil;
import com.cdac.exambackup.util.Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class FileExtensionServiceImpl extends AbstractBaseService<FileExtension, Long> implements FileExtensionService {
    final FileExtensionDao fileExtensionDao;


    protected FileExtensionServiceImpl(BaseDao<FileExtension, Long> baseDao, FileExtensionDao fileExtensionDao) {
        super(baseDao);
        this.fileExtensionDao = fileExtensionDao;
    }

    @Transactional
    @Override
    public FileExtension save(FileExtension fileExtension) {
        // new record
        if (fileExtension.getId() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(fileExtension.getCode(), fileExtension.getName())) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank.");
            }
            Util.isConvertibleToNumberElseThrowException("code", fileExtension.getCode());
            // try adding a new record (more performant)
            // if violation constraint exception is thrown then duplicate exists.
            try {
                fileExtension.setCode(fileExtension.getCode().trim());
                fileExtension.setName(fileExtension.getName().toUpperCase().trim());
                return fileExtensionDao.save(fileExtension);
            } catch (Exception ex) {
                log.info("Error occurred while creating a new role: {}", ex.getMessage());
                throw new InvalidReqPayloadException("Same 'name' or/and 'code' already exists.");
            }
        }
        // else updating existing record.
        // if both values are invalid throw error: one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(fileExtension.getCode(), fileExtension.getName())) {
            throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank");
        }

        FileExtension daoFileExtension = fileExtensionDao.findById(fileExtension.getId());
        if (daoFileExtension == null) {
            throw new EntityNotFoundException("FileExtension with id: " + fileExtension.getId() + " not found.");
        }

        if (fileExtension.getCode() != null) {
            if (fileExtension.getCode().isBlank()) {
                throw new InvalidReqPayloadException("code cannot be blank");
            }
            Util.isConvertibleToNumberElseThrowException("code", fileExtension.getCode());
            daoFileExtension.setCode(fileExtension.getCode().trim());
        }
        if (fileExtension.getName() != null) {
            if (fileExtension.getName().isBlank()) {
                throw new InvalidReqPayloadException("name cannot be blank.");
            }
            daoFileExtension.setName(fileExtension.getName().trim().toUpperCase());
        }
        // since the transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore, catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return fileExtensionDao.save(daoFileExtension);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<FileExtension>> getAllByPage(Pageable pageable) {
        Page<FileExtension> page = fileExtensionDao.getAllByPage(pageable);
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), page.getContent());
    }
}
