package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByCodeOrNameIgnoreCase(Integer code, String name);
}
