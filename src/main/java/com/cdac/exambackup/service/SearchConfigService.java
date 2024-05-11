package com.cdac.exambackup.service;

import com.cdac.exambackup.entity.SearchConfig;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

public interface SearchConfigService extends BaseService<SearchConfig, Long> {
    void dump(List<SearchConfig> searchConfigs);
}
