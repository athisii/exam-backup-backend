package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.Role;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface RoleService extends BaseService<Role, Long> {

    PageResDto<List<Role>> getAllByPage(Pageable pageable);

    Role getByCode(String code);
}
