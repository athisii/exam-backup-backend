package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.PasswordChangeDto;
import com.cdac.exambackup.dto.ResetPasswordDto;
import com.cdac.exambackup.entity.AppUser;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface AppUserService extends BaseService<AppUser, Long> {
    void changePassword(PasswordChangeDto passwordChangeDto);

    void resetPassword(ResetPasswordDto resetPasswordDto);

    void confirmPasswordReset(ResetPasswordDto resetPasswordDto);
}
