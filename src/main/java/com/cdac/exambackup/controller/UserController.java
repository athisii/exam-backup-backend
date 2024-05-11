package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.User;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.UserService;
import com.cdac.exambackup.util.JsonNodeUtil;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Tag(name = "User Controller")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/users")
public class UserController extends AbstractBaseController<User, Long> {
    static final SimpleBeanPropertyFilter commonPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "userId", "name", "role", "email", "mobileNumber", "examCentre", "code", "active", "createdDate", "modifiedDate");

    @Autowired
    UserService userService;


    public UserController(BaseService<User, Long> baseService) {
        super(baseService);
    }

    @Override
    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    public ResponseDto<?> get(@PathVariable("id") @Valid Long id) {
        log.info("Find Request for the User entity in the controller with id: {}", id);
        return new ResponseDto<>("Data fetched Successfully", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.userService.getById(id)));
    }

    @Override
    @GetMapping(produces = {"application/json"})
    public ResponseDto<?> getAll() {
        log.info("GetAll Request for the User entity in the controller");
        return new ResponseDto<>("Data fetched successfully", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.userService.getAll()));
    }

    @Override
    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> list(@RequestBody @Valid ListRequest listRequest) {
        log.info("List Request for the User entity in the controller");
        return new ResponseDto<>("Filtered List fetched successfully", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.userService.list(listRequest)));
    }

    @Override
    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody @Valid User entity) {
        log.info("Create Request for the User entity in the controller.");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>("Your data has been saved successfully", JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.userService.save(entity)));
    }
}
