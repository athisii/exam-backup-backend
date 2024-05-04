package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.SearchConfigDao;
import com.cdac.exambackup.entity.SearchConfig;
import com.cdac.exambackup.service.SearchConfigService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    SearchConfigDao searchConfigDao;


    public SearchConfigServiceImpl(BaseDao<SearchConfig, Long> baseService) {
        super(baseService);
    }

    public void dump() {
        List<SearchConfig> searchConfigs = new ArrayList<>();
        if (this.searchConfigDao.count() == 0L) {
            searchConfigs.add(new SearchConfig("User", "userId,name,email,mobileNumber"));
            searchConfigs.add(new SearchConfig("Role", "name"));
            searchConfigs.add(new SearchConfig("Region", "name"));
            searchConfigs.add(new SearchConfig("AuditModel", "name"));
            this.searchConfigDao.save(searchConfigs);
        }
    }
}
