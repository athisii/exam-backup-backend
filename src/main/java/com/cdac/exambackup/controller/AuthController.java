package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.PasswordChangeDto;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.service.AppUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author athisii
 * @version 1.0
 * @since 5/15/24
 */

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Authentication")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
public class AuthController {
    @Autowired
    AppUserService appUserService;

    @PostMapping(name = "/change-password", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> changePassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto) {
        appUserService.changePassword(passwordChangeDto);
        return new ResponseDto<>("Password changed successfully", null);
    }
}
