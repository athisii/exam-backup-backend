package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.FileExtensionDao;
import com.cdac.exambackup.dao.FileTypeDao;
import com.cdac.exambackup.dto.FileTypeReqDto;
import com.cdac.exambackup.dto.FileTypeResDto;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.FileExtension;
import com.cdac.exambackup.entity.FileType;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.FileTypeService;
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
public class FileTypeServiceImpl extends AbstractBaseService<FileType, Long> implements FileTypeService {
    final FileTypeDao fileTypeDao;
    final FileExtensionDao fileExtensionDao;

    public FileTypeServiceImpl(BaseDao<FileType, Long> baseDao, FileTypeDao fileTypeDao, FileExtensionDao fileExtensionDao) {
        super(baseDao);
        this.fileTypeDao = fileTypeDao;
        this.fileExtensionDao = fileExtensionDao;
    }

    @Override
    @Transactional
    public FileType save(FileTypeReqDto fileTypeReqDto) {
        // new record entry
        if (fileTypeReqDto.id() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(fileTypeReqDto.code(), fileTypeReqDto.name())) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank.");
            }
            if (fileTypeReqDto.fileExtensionId() == null) {
                throw new InvalidReqPayloadException("'fileExtensionId' cannot be null.");
            }

            FileExtension daoFileExtension = fileExtensionDao.findById(fileTypeReqDto.fileExtensionId());
            if (daoFileExtension == null) {
                throw new EntityNotFoundException("FileExtension with id: " + fileTypeReqDto.fileExtensionId() + " not found.");
            }
            Util.isConvertibleToNumberElseThrowException("code", fileTypeReqDto.code());
            // try adding a new record (more performant)
            // if violation constraint exception is thrown then duplicate exists.
            try {
                FileType fileType = new FileType();
                fileType.setCode(fileTypeReqDto.code().toUpperCase().trim());
                fileType.setName(fileTypeReqDto.name().toUpperCase().trim());
                fileType.setFileExtension(daoFileExtension);
                return fileTypeDao.save(fileType);
            } catch (Exception ex) {
                log.info("Error occurred while creating a new fileType: {}", ex.getMessage());
                throw new InvalidReqPayloadException("Same 'name' or/and 'code' already exists.");
            }
        }
        // else updating existing record.
        // if all values are invalid throw error; one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(fileTypeReqDto.code(), fileTypeReqDto.name()) && fileTypeReqDto.fileExtensionId() == null) {
            throw new InvalidReqPayloadException("Both 'code', 'name' and 'fileExtensionId' cannot be null or blank");
        }
        FileType daoFileType = fileTypeDao.findById(fileTypeReqDto.id());
        if (daoFileType == null) {
            throw new EntityNotFoundException("FileType with id: " + fileTypeReqDto.id() + " not found.");
        }
        if (fileTypeReqDto.code() != null) {
            if (fileTypeReqDto.code().isBlank()) {
                throw new InvalidReqPayloadException("code cannot be blank");
            }
            Util.isConvertibleToNumberElseThrowException("code", fileTypeReqDto.code());
            daoFileType.setCode(fileTypeReqDto.code().trim().toUpperCase());
        }
        if (fileTypeReqDto.name() != null) {
            if (fileTypeReqDto.name().isBlank()) {
                throw new InvalidReqPayloadException("name cannot be blank.");
            }
            daoFileType.setName(fileTypeReqDto.name().trim().toUpperCase());
        }
        if (fileTypeReqDto.fileExtensionId() != null) {
            FileExtension daoFileExtension = fileExtensionDao.findById(fileTypeReqDto.fileExtensionId());
            if (daoFileExtension == null) {
                throw new EntityNotFoundException("FileExtension with id: " + fileTypeReqDto.fileExtensionId() + " not found.");
            }
            daoFileType.setFileExtension(daoFileExtension);
        }
        // since the transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore, catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return fileTypeDao.save(daoFileType);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<FileTypeResDto>> getAllByPage(Pageable pageable) {
        Page<FileType> page = fileTypeDao.getAllByPage(pageable);
        List<FileTypeResDto> fileTypeResDtos = page.getContent().stream().map(fileType -> new FileTypeResDto(fileType.getId(), fileType.getCode(), fileType.getName(), fileType.getFileExtension().getId(), fileType.getCreatedDate(), fileType.getModifiedDate())).toList();
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), fileTypeResDtos);
    }

}
