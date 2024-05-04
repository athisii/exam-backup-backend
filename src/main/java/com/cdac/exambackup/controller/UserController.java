package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.User;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    UserService userService;

    public UserController(BaseService<User, Long> baseService) {
        super(baseService);
    }
}
