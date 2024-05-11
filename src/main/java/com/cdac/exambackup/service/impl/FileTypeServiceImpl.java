package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.FileTypeDao;
import com.cdac.exambackup.entity.FileType;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.FileTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    FileTypeDao fileTypeDao;

    public FileTypeServiceImpl(BaseDao<FileType, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public FileType save(FileType fileTypeDto) {
        /*
             if id not present in dto:
                  add new record after passing the constraint check.
             else:
                  if entity exist in table for passed id:
                       update only {code} and {name} after passing the constraint check. // other fields have separate API.
                  else:
                       throw exception.
         */

        // new record entry
        if (fileTypeDto.getId() == null) {
            // if both values are invalid, throw exception
            if (fileTypeDto.getCode() == null || fileTypeDto.getCode() <= 0 || fileTypeDto.getName() == null || fileTypeDto.getName().isBlank()) {
                throw new GenericException("Both 'code' and 'name' cannot be null or empty");
            }
            List<FileType> daoFileTypes = fileTypeDao.findByCodeOrName(fileTypeDto.getCode(), fileTypeDto.getName().trim());
            if (!daoFileTypes.isEmpty()) {
                throw new GenericException("Same 'code' or 'name' already exists");
            }
            // now remove the unnecessary fields if present or create new object.
            FileType fileType = new FileType();
            fileType.setCode(fileTypeDto.getCode());
            fileType.setName(fileTypeDto.getName().trim().toUpperCase());
            return fileTypeDao.save(fileType);
        }
        // else updating existing record.

        FileType daoFileType = fileTypeDao.findById(fileTypeDto.getId());
        if (daoFileType == null) {
            throw new EntityNotFoundException("FileType with id: " + fileTypeDto.getId() + " not found.");
        }
        if (Boolean.FALSE.equals(daoFileType.getActive())) {
            throw new EntityNotFoundException("FileType with id: " + daoFileType.getId() + " is not active. Must activate first.");
        }

        // if both values are invalid, one should be valid
        if ((fileTypeDto.getCode() == null && fileTypeDto.getName() == null) || (fileTypeDto.getCode() != null && fileTypeDto.getCode() <= 0 && fileTypeDto.getName() != null && fileTypeDto.getName().isBlank())) {
            throw new GenericException("Both 'code' and 'name' cannot be null or empty");
        }

        List<FileType> daoOtherFileTypes;
        if (fileTypeDto.getName() == null) {
            daoOtherFileTypes = fileTypeDao.findByCodeOrName(fileTypeDto.getCode(), null);
        } else {
            daoOtherFileTypes = fileTypeDao.findByCodeOrName(fileTypeDto.getCode(), fileTypeDto.getName().trim());
        }
        // check if it's the different object
        if ((daoOtherFileTypes != null && daoOtherFileTypes.size() > 1) || daoOtherFileTypes != null && !daoOtherFileTypes.isEmpty() && !daoFileType.getId().equals(daoOtherFileTypes.getFirst().getId())) {
            throw new GenericException("Same 'code' or 'name' already exists");
        }

        if (fileTypeDto.getCode() != null) {
            if (fileTypeDto.getCode() <= 0) {
                throw new GenericException("code must be greater than 0");
            }
            daoFileType.setCode(fileTypeDto.getCode());
        }
        if (fileTypeDto.getName() != null) {
            if (fileTypeDto.getName().isBlank()) {
                throw new GenericException("name cannot be empty.");
            }
            daoFileType.setName(fileTypeDto.getName().trim().toUpperCase());
        }
        return fileTypeDao.save(daoFileType);
    }
}
