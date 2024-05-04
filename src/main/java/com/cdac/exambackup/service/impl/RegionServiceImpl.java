package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.RegionDao;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.service.RegionService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
