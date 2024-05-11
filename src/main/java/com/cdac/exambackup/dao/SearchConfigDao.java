package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.SearchConfig;

public interface SearchConfigDao extends BaseDao<SearchConfig, Long> {
    SearchConfig findByEntityName(String name);
}
