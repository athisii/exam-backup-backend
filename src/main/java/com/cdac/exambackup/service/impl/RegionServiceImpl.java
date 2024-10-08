package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.RegionDao;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.RegionService;
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
public class RegionServiceImpl extends AbstractBaseService<Region, Long> implements RegionService {
    final RegionDao regionDao;

    public RegionServiceImpl(BaseDao<Region, Long> baseDao, RegionDao regionDao) {
        super(baseDao);
        this.regionDao = regionDao;
    }

    @Transactional
    @Override
    public Region save(Region region) {
        // new record entry
        if (region.getId() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(region.getCode(), region.getName())) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank.");
            }
            Util.isConvertibleToNumberElseThrowException("code", region.getCode());
            // try adding a new record (more performant)
            // if violation constraint exception is thrown then duplicate exists.
            try {
                region.setCode(region.getCode().toUpperCase().trim());
                region.setName(region.getName().toUpperCase().trim());
                return regionDao.save(region);
            } catch (Exception ex) {
                log.info("Error occurred while creating a new region: {}", ex.getMessage());
                throw new InvalidReqPayloadException("Same 'name' or/and 'code' already exists.");
            }
        }
        // else updating existing record.
        // if both values are invalid throw error; one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(region.getCode(), region.getName())) {
            throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank");
        }
        Region daoRegion = regionDao.findById(region.getId());
        if (daoRegion == null) {
            throw new EntityNotFoundException("Region with id: " + region.getId() + " not found.");
        }
        if (region.getCode() != null) {
            if (region.getCode().isBlank()) {
                throw new InvalidReqPayloadException("code cannot be blank");
            }
            Util.isConvertibleToNumberElseThrowException("code", region.getCode());
            daoRegion.setCode(region.getCode().trim().toUpperCase());
        }
        if (region.getName() != null) {
            if (region.getName().isBlank()) {
                throw new InvalidReqPayloadException("name cannot be blank.");
            }
            daoRegion.setName(region.getName().trim().toUpperCase());
        }
        // since the transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore, catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return regionDao.save(daoRegion);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<Region>> getAllByPage(Pageable pageable) {
        Page<Region> page = regionDao.getAllByPage(pageable);
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), page.getContent());
    }
}
