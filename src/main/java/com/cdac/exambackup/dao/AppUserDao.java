package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.PasswordResetOtp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    Page<AppUser> getAllByPage(Pageable pageable, List<String> roleCodes);

    List<AppUser> getAllRegionHead();
}
