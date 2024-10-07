package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.RoleDao;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.RoleService;
import com.cdac.exambackup.util.NullAndBlankUtil;
import com.cdac.exambackup.util.Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    final RoleDao roleDao;

    public RoleServiceImpl(BaseDao<Role, Long> baseDao, RoleDao roleDao) {
        super(baseDao);
        this.roleDao = roleDao;
    }

    @Transactional
    @Override
    public Role save(Role role) {
        // new record entry
        if (role.getId() == null) {
            // if both values are invalid, throw exception
            if (NullAndBlankUtil.isAnyNullOrBlank(role.getCode(), role.getName())) {
                throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank.");
            }
            Util.isConvertibleToNumberElseThrowException("code", role.getCode());
            // try adding a new record (more performant)
            // if violation constraint exception is thrown then duplicate exists.
            try {
                role.setCode(role.getCode().toUpperCase().trim());
                role.setName(role.getName().toUpperCase().trim());
                return roleDao.save(role);
            } catch (Exception ex) {
                log.info("Error occurred while creating a new role: {}", ex.getMessage());
                throw new InvalidReqPayloadException("Same 'name' or/and 'code' already exists.");
            }
        }
        // else updating existing record.
        // if both values are invalid throw error; one should be valid
        if (NullAndBlankUtil.isAllNullOrBlank(role.getCode(), role.getName())) {
            throw new InvalidReqPayloadException("Both 'code' and 'name' cannot be null or blank");
        }

        Role daoRole = roleDao.findById(role.getId());
        if (daoRole == null) {
            throw new EntityNotFoundException("Role with id: " + role.getId() + " not found.");
        }

        if (role.getCode() != null) {
            if (role.getCode().isBlank()) {
                throw new InvalidReqPayloadException("code cannot be blank");
            }
            Util.isConvertibleToNumberElseThrowException("code", role.getCode());
            daoRole.setCode(role.getCode().trim().toUpperCase());
        }
        if (role.getName() != null) {
            if (role.getName().isBlank()) {
                throw new InvalidReqPayloadException("name cannot be blank.");
            }
            daoRole.setName(role.getName().trim().toUpperCase());
        }
        // since the transaction is enabled, unique constraints violation will be caught at commit phase,
        // so can't be caught, therefore, catch it in global exception handler (ControllerAdvice)
        // this object is already mapped to row in the table (has id)
        return roleDao.save(daoRole);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResDto<List<Role>> getAllByPage(Pageable pageable) {
        Page<Role> page = roleDao.getAllByPage(pageable);
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), page.getContent());
    }
}
