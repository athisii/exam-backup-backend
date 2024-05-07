package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.RoleDao;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    @Override
    public Role save(Role roleDto) {
        /*
             if id not present in dto:
                  add new record after passing the constraint check.
             else:
                  if entity exist in table for passed id:
                       update only {code} and {name} after passing the constraint check. // other fields have separate API.
                  else:
                       throw exception.
         */

        // new record entry
        if (roleDto.getId() == null) {
            // if both values are invalid, throw exception
            if (roleDto.getCode() == null || roleDto.getCode() <= 0 || roleDto.getName() == null || roleDto.getName().isBlank()) {
                throw new GenericException("Both 'code' and 'name' cannot be null or empty");
            }
            List<Role> daoRoles = roleDao.findByCodeOrName(roleDto.getCode(), roleDto.getName().trim());
            if (!daoRoles.isEmpty()) {
                throw new GenericException("Same 'code' or 'name' already exists");
            }
            // now remove the unnecessary fields if present or create new object.
            Role role = new Role();
            role.setCode(roleDto.getCode());
            role.setName(roleDto.getName().trim().toUpperCase());
            return roleDao.save(role);
        }
        // else updating existing record.

        Role daoRole = roleDao.findById(roleDto.getId());
        if (daoRole == null) {
            throw new EntityNotFoundException("Role with id: " + roleDto.getId() + " not found.");
        }

        // if both values are invalid, one should be valid
        if ((roleDto.getCode() == null && roleDto.getName() == null) || (roleDto.getCode() != null && roleDto.getCode() <= 0 && roleDto.getName() != null && roleDto.getName().isBlank())) {
            throw new GenericException("Both 'code' and 'name' cannot be null or empty");
        }

        List<Role> daoOtherRoles;
        if (roleDto.getName() == null) {
            daoOtherRoles = roleDao.findByCodeOrName(roleDto.getCode(), null);
        } else {
            daoOtherRoles = roleDao.findByCodeOrName(roleDto.getCode(), roleDto.getName().trim());
        }
        // check if it's the different object
        if ((daoOtherRoles != null && daoOtherRoles.size() > 1) || daoOtherRoles != null && !daoOtherRoles.isEmpty() && daoRole != daoOtherRoles.getFirst()) {
            throw new GenericException("Same 'code' or 'name' already exists");
        }

        if (roleDto.getCode() != null) {
            if (roleDto.getCode() <= 0) {
                throw new GenericException("code must be greater than 0");
            }
            daoRole.setCode(roleDto.getCode());
        }
        if (roleDto.getName() != null) {
            if (roleDto.getName().isBlank()) {
                throw new GenericException("name cannot be empty.");
            }
            daoRole.setName(roleDto.getName().trim().toUpperCase());
        }
        return roleDao.save(daoRole);
    }
}
