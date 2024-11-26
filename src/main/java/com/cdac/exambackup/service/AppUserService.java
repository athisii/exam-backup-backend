package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.*;
import com.cdac.exambackup.entity.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface AppUserService extends BaseService<AppUser, Long> {
    void changePassword(PasswordChangeDto passwordChangeDto);

    void resetPassword(ResetPasswordDto resetPasswordDto);

    void confirmPasswordReset(ResetPasswordDto resetPasswordDto);

    AppUser save(AppUserReqDto appUserReqDto);

    PageResDto<List<AppUserResDto>> getAllByPage(Pageable pageable);

    void bulkUpload(MultipartFile file);
}
