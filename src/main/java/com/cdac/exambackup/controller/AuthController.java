package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.*;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.AppUserService;
import com.cdac.exambackup.util.JwtProvider;
import com.cdac.exambackup.util.NullAndBlankUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${jwt.access.token.validity}")
    private Long accessTokenValidity;

    @Value("${jwt.refresh.token.validity}")
    private Long refreshTokenValidity;

    @Autowired
    AppUserService appUserService;
    @Autowired
    JwtProvider jwtProvider;

    @PostMapping(value = "/change-password", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        appUserService.changePassword(passwordChangeDto);
        return new ResponseDto<>("Password changed successfully.", null);
    }

    @PostMapping("/password-reset/initiate")
    public ResponseDto<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        appUserService.resetPassword(resetPasswordDto);
        return new ResponseDto<>("OTP sent successfully.", null);
    }

    @PostMapping("/password-reset/confirm")
    public ResponseDto<?> confirmPasswordReset(@RequestBody ResetPasswordDto resetPasswordDto) {
        appUserService.confirmPasswordReset(resetPasswordDto);
        return new ResponseDto<>("Password reset successfully.", null);
    }

    @PostMapping("/refresh-token")
    public ResponseDto<?> refreshToken(@RequestBody TokenReqDto tokenReqDto) {
        if (NullAndBlankUtil.isAnyNullOrBlank(tokenReqDto.refreshToken())) {
            throw new InvalidReqPayloadException("'refreshToken' should not be null or blank.");
        }
        try {
            String token = jwtProvider.generateTokenFromToken(tokenReqDto.refreshToken(), accessTokenValidity);
            String refreshToken = jwtProvider.generateTokenFromToken(tokenReqDto.refreshToken(), refreshTokenValidity);
            return new ResponseDto<>("Token refreshed successfully.", new TokenResDto(token, refreshToken, false));
        } catch (Exception ex) {
            log.error("Invalid refresh token. {}", ex.getMessage());
            return new ResponseDto<>("Invalid refresh token.", false, null);
        }
    }
}
