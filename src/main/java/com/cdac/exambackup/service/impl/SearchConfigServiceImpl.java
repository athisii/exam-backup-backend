package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.SearchConfigDao;
import com.cdac.exambackup.entity.SearchConfig;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.SearchConfigService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class SearchConfigServiceImpl extends AbstractBaseService<SearchConfig, Long> implements SearchConfigService {
    final SearchConfigDao searchConfigDao;

    public SearchConfigServiceImpl(BaseDao<SearchConfig, Long> baseService, SearchConfigDao searchConfigDao) {
        super(baseService);
        this.searchConfigDao = searchConfigDao;
    }

    @Transactional
    @Override
    public void dump(List<SearchConfig> searchConfigs) {
        searchConfigs.forEach(searchConfig -> {
            SearchConfig daoSearchConfig = searchConfigDao.findByEntityName(searchConfig.getEntityName());
            if (daoSearchConfig != null && !daoSearchConfig.getId().equals(searchConfig.getId())) {
                throw new GenericException("SearchConfig with name: " + searchConfig.getEntityName() + " already exists");
            }
        });
        this.searchConfigDao.save(searchConfigs);
    }
}
