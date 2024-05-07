package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.Role;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface RoleDao extends BaseDao<Role, Long> {
    List<Role> findByCodeOrName(Integer code, String name);

    Role findByName(String name);
}
