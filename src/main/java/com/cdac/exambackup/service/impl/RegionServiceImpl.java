package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.RegionDao;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.RegionService;
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
public class RegionServiceImpl extends AbstractBaseService<Region, Long> implements RegionService {
    @Autowired
    RegionDao regionDao;

    public RegionServiceImpl(BaseDao<Region, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public Region save(Region regionDto) {
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
        if (regionDto.getId() == null) {
            // if both values are invalid, throw exception
            if (regionDto.getCode() == null || regionDto.getCode() <= 0 || regionDto.getName() == null || regionDto.getName().isBlank()) {
                throw new GenericException("Both 'code' and 'name' cannot be null or empty");
            }
            List<Region> daoRegions = regionDao.findByCodeOrName(regionDto.getCode(), regionDto.getName().trim());
            if (!daoRegions.isEmpty()) {
                throw new GenericException("Same 'code' or 'name' already exists");
            }
            // now remove the unnecessary fields if present or create new object.
            Region region = new Region();
            region.setCode(regionDto.getCode());
            region.setName(regionDto.getName().trim().toUpperCase());
            return regionDao.save(region);
        }
        // else updating existing record.

        Region daoRegion = regionDao.findById(regionDto.getId());
        if (daoRegion == null) {
            throw new EntityNotFoundException("Region with id: " + regionDto.getId() + " not found.");
        }

        // if both values are invalid, one should be valid
        if ((regionDto.getCode() == null && regionDto.getName() == null) || (regionDto.getCode() != null && regionDto.getCode() <= 0 && regionDto.getName() != null && regionDto.getName().isBlank())) {
            throw new GenericException("Both 'code' and 'name' cannot be null or empty");
        }

        List<Region> daoOtherRegions;
        if (regionDto.getName() == null) {
            daoOtherRegions = regionDao.findByCodeOrName(regionDto.getCode(), null);
        } else {
            daoOtherRegions = regionDao.findByCodeOrName(regionDto.getCode(), regionDto.getName().trim());
        }
        // check if it's the different object
        if ((daoOtherRegions != null && daoOtherRegions.size() > 1) || daoOtherRegions != null && !daoOtherRegions.isEmpty() && daoRegion != daoOtherRegions.getFirst()) {
            throw new GenericException("Same 'code' or 'name' already exists");
        }

        if (regionDto.getCode() != null) {
            if (regionDto.getCode() <= 0) {
                throw new GenericException("code must be greater than 0");
            }
            daoRegion.setCode(regionDto.getCode());
        }
        if (regionDto.getName() != null) {
            if (regionDto.getName().isBlank()) {
                throw new GenericException("name cannot be empty.");
            }
            daoRegion.setName(regionDto.getName().trim().toUpperCase());
        }
        return regionDao.save(daoRegion);
    }
}
