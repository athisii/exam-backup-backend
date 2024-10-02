package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.FileTypeDao;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.FileType;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.FileTypeService;
import com.cdac.exambackup.util.NullAndBlankUtil;
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
public class FileTypeServiceImpl extends AbstractBaseService<FileType, Long> implements FileTypeService {
    final FileTypeDao fileTypeDao;

    public FileTypeServiceImpl(BaseDao<FileType, Long> baseDao, FileTypeDao fileTypeDao) {
        super(baseDao);
        this.fileTypeDao = fileTypeDao;
    }

    @Transactional
    @Override
    public FileType save(FileType fileType) {
        // new record entry
        if (fileType.getId() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(fileType.getCode(), fileType.getName())) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank.");
            }
            // try adding a new record (more performant)
            // if violation constraint exception is thrown then duplicate exists.
            try {
                fileType.setCode(fileType.getCode().toUpperCase().trim());
                fileType.setName(fileType.getName().toUpperCase().trim());
                return fileTypeDao.save(fileType);
            } catch (Exception ex) {
                log.info("Error occurred while creating a new fileType: {}", ex.getMessage());
                throw new InvalidReqPayloadException("Same 'name' or/and 'code' already exists.");
            }
        }
        // else updating existing record.
        // if both values are invalid throw error; one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(fileType.getCode(), fileType.getName())) {
            throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank");
        }
        FileType daoFileType = fileTypeDao.findById(fileType.getId());
        if (daoFileType == null) {
            throw new EntityNotFoundException("FileType with id: " + fileType.getId() + " not found.");
        }
        if (fileType.getCode() != null) {
            if (fileType.getCode().isBlank()) {
                throw new InvalidReqPayloadException("code cannot be blank");
            }
            daoFileType.setCode(fileType.getCode().trim().toUpperCase());
        }
        if (fileType.getName() != null) {
            if (fileType.getName().isBlank()) {
                throw new InvalidReqPayloadException("name cannot be blank.");
            }
            daoFileType.setName(fileType.getName().trim().toUpperCase());
        }
        // since the transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore, catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return fileTypeDao.save(daoFileType);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<FileType>> getAllByPage(Pageable pageable) {
        Page<FileType> page = fileTypeDao.getAllByPage(pageable);
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), page.getContent());
    }
}
