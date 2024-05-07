package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Tag(name = "Role Controller")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/roles")
public class RoleController extends AbstractBaseController<Role, Long> {
    @Autowired
    RoleService roleService;

    public RoleController(BaseService<Role, Long> baseService) {
        super(baseService);
    }

    @Override
    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody @Valid Role role) {
        // TODO:: log user id?
        log.info("Create request for the entity by userId: ");
        return new ResponseDto<>("Your data has been saved successfully", this.roleService.save(role).getId());
    }

}
