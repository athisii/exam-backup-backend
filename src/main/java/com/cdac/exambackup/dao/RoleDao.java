package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface RoleDao extends BaseDao<Role, Long> {
    List<Role> findByCodeOrName(String code, String name);

    Role findByName(String name);

    Page<Role> getAllByPage(Pageable pageable);

    Role getByCode(String code);
}
