package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByCodeOrNameIgnoreCase(Integer code, String name);

    Role findFirstByNameIgnoreCase(String name);
}
