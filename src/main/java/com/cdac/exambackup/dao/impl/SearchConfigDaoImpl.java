package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.SearchConfigDao;
import com.cdac.exambackup.dao.repo.SearchConfigRepository;
import com.cdac.exambackup.entity.SearchConfig;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class SearchConfigDaoImpl extends AbstractBaseDao<SearchConfig, Long> implements SearchConfigDao {
    @Autowired
    SearchConfigRepository searchConfigRepository;

    @Override
    public JpaRepository<SearchConfig, Long> getRepository() {
        return this.searchConfigRepository;
    }

    @Override
    public Class<SearchConfig> getEntityClass() {
        return SearchConfig.class;
    }
}
