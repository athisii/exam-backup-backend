package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dto.PasswordChangeDto;
import com.cdac.exambackup.dto.ResetPasswordDto;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.PasswordResetOtp;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.AppUserService;
import com.cdac.exambackup.service.EmailService;
import com.cdac.exambackup.util.NullAndBlankUtil;
import com.cdac.exambackup.util.Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AppUserServiceImpl extends AbstractBaseService<AppUser, Long> implements AppUserService {
    private static final char[] ALPHANUMERIC = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    private static final int OTP_LENGTH = 6;
    private static final Random random = new Random();

    @Autowired
    AppUserDao appUserDao;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EmailService emailService;

    public AppUserServiceImpl(BaseDao<AppUser, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public AppUser save(AppUser appUserDto) {
        // for admin
        if ("admin".equals(appUserDto.getUserId()) && appUserDao.count() == 0L) {
            appUserDao.save(appUserDto);
            return appUserDto;
        }

        if (appUserDto.getId() == null && appUserDto.getUserId() == null) {
            throw new GenericException("Both id and userId can not be null or empty");
        }
        if ((appUserDto.getEmail() == null || appUserDto.getEmail().isBlank()) && (appUserDto.getMobileNumber() == null || appUserDto.getMobileNumber().isBlank())) {
            throw new GenericException("Both email and mobile can not be null or empty. At least one of them must be provided");
        }
        AppUser daoAppUser = null;
        if (appUserDto.getId() != null) {
            daoAppUser = appUserDao.findById(appUserDto.getId());
        }
        //  if not found with id, search with userId
        if (daoAppUser == null && appUserDto.getUserId() != null) {
            daoAppUser = appUserDao.findByUserId(appUserDto.getUserId());
        }
        if (daoAppUser == null || Boolean.TRUE.equals(daoAppUser.getDeleted())) {
            throw new EntityNotFoundException("User not found.");
        }
        if (Boolean.FALSE.equals(daoAppUser.getActive())) {
            throw new EntityNotFoundException("ExamCentre with id: " + daoAppUser.getId() + " is not active. Must activate first.");
        }
        if (appUserDto.getMobileNumber() != null) {
            if (!Util.validateMobileNumber(appUserDto.getMobileNumber())) {
                throw new GenericException("Malformed mobile number.");
            }
            daoAppUser.setMobileNumber(appUserDto.getMobileNumber());
        }
        if (appUserDto.getEmail() != null) {
            if (!Util.validateEmail(appUserDto.getEmail())) {
                throw new GenericException("Malformed email address.");
            }
            daoAppUser.setEmail(appUserDto.getEmail().trim());
        }
        return appUserDao.save(daoAppUser);
    }

    @Override
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        if (passwordChangeDto == null || passwordChangeDto.userId() == null || passwordChangeDto.oldPassword() == null || passwordChangeDto.newPassword() == null) {
            throw new GenericException("userId, oldPassword and newPassword can not be null or empty");
        }
        if (passwordChangeDto.oldPassword().equals(passwordChangeDto.newPassword())) {
            throw new GenericException("oldPassword and newPassword should not match.");
        }
        AppUser daoAppUser = appUserDao.findByUserId(passwordChangeDto.userId());
        if (daoAppUser == null || Boolean.TRUE.equals(daoAppUser.getDeleted()) || Boolean.FALSE.equals(daoAppUser.getActive())) {
            throw new EntityNotFoundException("User not found with userId: " + passwordChangeDto.userId());
        }

        if (!passwordEncoder.matches(passwordChangeDto.oldPassword(), daoAppUser.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }
        daoAppUser.setPassword(passwordEncoder.encode(passwordChangeDto.newPassword()));
        appUserDao.save(daoAppUser);
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        if (NullAndBlankUtil.isAnyNullOrBlank(resetPasswordDto.userId())) {
            throw new GenericException("'userId' can not be null or empty");
        }
        AppUser appUser = appUserDao.findByUserId(resetPasswordDto.userId());
        if (appUser == null) {
            throw new EntityNotFoundException("User not found with userId: " + resetPasswordDto.userId());
        }
        if (NullAndBlankUtil.isAnyNullOrBlank(appUser.getEmail())) {
            throw new EntityNotFoundException("User userId: " + resetPasswordDto.userId() + " does not have registered email.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            stringBuilder.append(ALPHANUMERIC[random.nextInt(ALPHANUMERIC.length)]);
        }
        PasswordResetOtp passwordResetOtp = appUserDao.findPasswordResetOtp(resetPasswordDto.userId());
        if (passwordResetOtp == null) {
            passwordResetOtp = new PasswordResetOtp();
            passwordResetOtp.setUserId(resetPasswordDto.userId());
        }
        passwordResetOtp.setOtp(passwordEncoder.encode(stringBuilder.toString()));
        passwordResetOtp.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        appUserDao.resetPassword(passwordResetOtp);

        String template = """
                Dear user,
                We have received a request to reset your password. To proceed, please use the following One-Time Password(OTP): %s
                If you did not request this, please contact our support team immediately.
                
                Best regards,
                Exam Backup Team
                """;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(appUser.getEmail());
        message.setSubject("Password Reset OTP");
        message.setText(String.format(template, stringBuilder));
        Thread.ofVirtual().start(() -> emailService.send(message));
    }


    @Transactional
    @Override
    public void confirmPasswordReset(ResetPasswordDto resetPasswordDto) {
        if (NullAndBlankUtil.isAnyNullOrBlank(resetPasswordDto.userId(), resetPasswordDto.otp(), resetPasswordDto.password())) {
            throw new GenericException("'userId', 'otp' and 'password' can not be null or empty");
        }
        PasswordResetOtp daoPasswordResetOtp = appUserDao.findPasswordResetOtp(resetPasswordDto.userId());
        if (daoPasswordResetOtp == null) {
            throw new EntityNotFoundException("No OTP found with userId: " + resetPasswordDto.userId());
        }
        if (!passwordEncoder.matches(resetPasswordDto.otp(), daoPasswordResetOtp.getOtp())) {
            throw new BadCredentialsException("Invalid OTP");
        }
        if (LocalDateTime.now().isAfter(daoPasswordResetOtp.getExpiryDate())) {
            throw new BadCredentialsException("OTP already expired.");
        }
        AppUser daoAppUser = appUserDao.findByUserId(resetPasswordDto.userId());
        if (daoAppUser == null) {
            throw new EntityNotFoundException("User not found with userId: " + resetPasswordDto.userId());
        }
        daoAppUser.setPassword(passwordEncoder.encode(resetPasswordDto.password()));
        appUserDao.save(daoAppUser);
        appUserDao.deletePasswordResetOtpByUserId(daoPasswordResetOtp.getUserId());
    }
}
