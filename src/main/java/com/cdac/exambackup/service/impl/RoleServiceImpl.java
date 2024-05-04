package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.RoleDao;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.service.RoleService;
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
public class RoleServiceImpl extends AbstractBaseService<Role, Long> implements RoleService {
    @Autowired
    RoleDao roleDao;

    public RoleServiceImpl(BaseDao<Role, Long> baseDao) {
        super(baseDao);
    }
}
