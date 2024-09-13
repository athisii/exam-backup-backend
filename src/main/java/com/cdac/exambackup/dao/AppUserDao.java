package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.PasswordResetOtp;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface AppUserDao extends BaseDao<AppUser, Long> {
    AppUser findByUserId(String userId);

    void resetPassword(PasswordResetOtp passwordResetOtp);

    PasswordResetOtp findPasswordResetOtp(String userId);

    void deletePasswordResetOtpByUserId(String userId);
}
