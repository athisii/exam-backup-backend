package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.RoleDao;
import com.cdac.exambackup.dao.repo.RoleRepository;
import com.cdac.exambackup.entity.Role;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class RoleDaoImpl extends AbstractBaseDao<Role, Long> implements RoleDao {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public JpaRepository<Role, Long> getRepository() {
        return this.roleRepository;
    }

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    public void softDelete(Role entity) {
        if (entity != null) {
            entity.setDeleted(true);
            entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
            entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());
            roleRepository.save(entity);
        }
    }

    @Override
    public void softDelete(Collection<Role> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(entity -> {
                entity.setDeleted(true);
                entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
                entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());
            });
            roleRepository.saveAll(entities);
        }
    }

    @Override
    public List<Role> findByCodeOrName(String code, String name) {
        return this.roleRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name);
    }

    @Override
    public Role findByName(String name) {
        return this.roleRepository.findFirstByNameIgnoreCase(name);
    }
}
