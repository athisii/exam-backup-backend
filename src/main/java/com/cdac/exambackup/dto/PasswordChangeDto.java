package com.cdac.exambackup.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author athisii
 * @version 1.0
 * @since 5/15/24
 */

public record PasswordChangeDto(@NotBlank String userId, @NotBlank String oldPassword, @NotBlank String newPassword) {
}
