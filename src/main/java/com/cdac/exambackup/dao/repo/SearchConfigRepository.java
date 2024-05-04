package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.SearchConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Repository
public interface SearchConfigRepository extends JpaRepository<SearchConfig, Long> {
}
