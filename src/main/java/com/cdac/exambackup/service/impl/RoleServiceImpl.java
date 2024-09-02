package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.RoleDao;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.RoleService;
import com.cdac.exambackup.util.NullAndBlankUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // new record entry
        if (roleDto.getId() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(roleDto.getCode(), roleDto.getName())) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank.");
            }
            // try adding a new record (more performant)
            // if violation constraint exception is thrown then duplicate exists.
            try {
                roleDto.setCode(roleDto.getCode().toUpperCase().trim());
                roleDto.setName(roleDto.getName().toUpperCase().trim());
                return roleDao.save(roleDto);
            } catch (Exception ex) {
                log.info("Error occurred while creating a new role: {}", ex.getMessage());
                throw new InvalidReqPayloadException("Same 'name' or/and 'code' already exists.");
            }
        }
        // else updating existing record.
        // if both values are invalid, one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(roleDto.getCode(), roleDto.getName())) {
            throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank");
        }

        Role daoRole = roleDao.findById(roleDto.getId());
        if (daoRole == null) {
            throw new EntityNotFoundException("Role with id: " + roleDto.getId() + " not found.");
        }

        if (roleDto.getCode() != null) {
            if (roleDto.getCode().isBlank()) {
                throw new InvalidReqPayloadException("code cannot be blank");
            }
            daoRole.setCode(roleDto.getCode().trim().toUpperCase());
        }
        if (roleDto.getName() != null) {
            if (roleDto.getName().isBlank()) {
                throw new InvalidReqPayloadException("name cannot be blank.");
            }
            daoRole.setName(roleDto.getName().trim().toUpperCase());
        }
        try {
            return roleDao.save(daoRole);
        } catch (Exception ex) {
            log.info("Error occurred while updating a role: {}", ex.getMessage());
            throw new InvalidReqPayloadException("Same 'name' or/and 'code' already exists.");
        }
    }
}
