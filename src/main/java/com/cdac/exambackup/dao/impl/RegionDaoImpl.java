package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.RegionDao;
import com.cdac.exambackup.dao.repo.RegionRepository;
import com.cdac.exambackup.entity.Region;
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
public class RegionDaoImpl extends AbstractBaseDao<Region, Long> implements RegionDao {
    @Autowired
    RegionRepository regionRepository;

    @Override
    public JpaRepository<Region, Long> getRepository() {
        return this.regionRepository;
    }

    @Override
    public Class<Region> getEntityClass() {
        return Region.class;
    }

    @Override
    public void softDelete(Region entity) {
        if (entity != null) {
            entity.setDeleted(true);
            entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
            entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());
            regionRepository.save(entity);
        }
    }

    @Override
    public void softDelete(Collection<Region> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(entity -> {
                entity.setDeleted(true);
                entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
                entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());
            });
            regionRepository.saveAll(entities);
        }
    }

    @Override
    public List<Region> findByCodeOrName(String code, String name) {
        return this.regionRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name);
    }
}
