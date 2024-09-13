package com.cdac.exambackup.dto;

/**
 * @author athisii
 * @version 1.0
 * @since 9/13/24
 */


public record ResetPasswordDto(String userId, String otp, String password) {
}
