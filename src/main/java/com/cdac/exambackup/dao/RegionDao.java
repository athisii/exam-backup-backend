package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.Region;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface RegionDao extends BaseDao<Region, Long> {
    List<Region> findByCodeOrName(String code, String name);
}
