package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.RegionDao;
import com.cdac.exambackup.dao.repo.ExamCentreRepository;
import com.cdac.exambackup.dao.repo.RegionRepository;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.entity.Role;
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
public class RegionDaoImpl extends AbstractBaseDao<Region, Long> implements RegionDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    RegionRepository regionRepository;
    @Autowired
    ExamCentreRepository examCentreRepository;

    @Override
    public JpaRepository<Region, Long> getRepository() {
        return this.regionRepository;
    }

    @Override
    public Class<Region> getEntityClass() {
        return Region.class;
    }

    private void markDeletedAndAddSuffix(Region region) {
        if (examCentreRepository.existsByRegionIdAndDeletedFalse(region.getId())) {
            throw new InvalidReqPayloadException("Region code: " + region.getCode() + " is associated with some exam centres. Cannot delete it.");
        }
        region.setDeleted(true);
        // user should be allowed to add same name after deleted
        // add suffix to avoid unique constraint violation for code
        region.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + region.getCode());
        // add suffix to avoid unique constraint violation for name
        region.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + region.getName());
    }

    @Override
    public void softDelete(Region region) {
        markDeletedAndAddSuffix(region);
        regionRepository.save(region);
    }

    @Override
    public void softDelete(Collection<Region> regions) {
        if (regions != null && !regions.isEmpty()) {
            regions.forEach(this::markDeletedAndAddSuffix);
            regionRepository.saveAll(regions);
        }
    }

    @Override
    public List<Region> findByCodeOrName(String code, String name) {
        return this.regionRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name);
    }

    @Override
    public Page<Region> getAllByPage(Pageable pageable) {
        try {
            return this.regionRepository.findByDeletedFalse(pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }
}
