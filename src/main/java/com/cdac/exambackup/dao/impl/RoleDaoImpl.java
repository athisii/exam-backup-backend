package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.RoleDao;
import com.cdac.exambackup.dao.repo.AppUserRepository;
import com.cdac.exambackup.dao.repo.RoleRepository;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Value("${role.admin.code}")
    String adminCode;

    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AppUserRepository appUserRepository;

    @Override
    public JpaRepository<Role, Long> getRepository() {
        return this.roleRepository;
    }

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Transactional
    @Override
    public void softDelete(Role role) {
        markDeletedAndAddSuffix(role);
        roleRepository.save(role);
    }

    @Transactional
    @Override
    public void softDelete(Collection<Role> roles) {
        if (roles != null && !roles.isEmpty()) {
            roles.forEach(this::markDeletedAndAddSuffix);
            roleRepository.saveAll(roles);
        }
    }

    @Override
    public List<Role> findByCodeOrName(String code, String name) {
        return this.roleRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name);
    }

    @Override
    public Role findByName(String name) {
        return this.roleRepository.findFirstByNameIgnoreCaseAndDeletedFalse(name);
    }

    private void markDeletedAndAddSuffix(Role role) {
        if (appUserRepository.existsByRoleIdAndDeletedFalse(role.getId())) {
            throw new InvalidReqPayloadException("Role code: " + role.getCode() + " is associated with some users. Cannot delete it.");
        }
        role.setDeleted(true);
        // user should be allowed to add same name after deleted
        // add suffix to avoid unique constraint violation for code
        role.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + role.getCode());
        // add suffix to avoid unique constraint violation for name
        role.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + role.getName());
    }

    @Override
    public Page<Role> getAllByPage(Pageable pageable) {
        try {
            return this.roleRepository.findByDeletedFalseAndNotAdmin(pageable, adminCode);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Role getByCode(String code) {
        return this.roleRepository.findFirstByCodeIgnoreCaseAndDeletedFalse(code);
    }
}
