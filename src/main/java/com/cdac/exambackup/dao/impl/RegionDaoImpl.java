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
    public List<Region> findByCodeOrName(Integer code, String name) {
        return this.regionRepository.findByCodeOrNameIgnoreCase(code, name);
    }
}
