package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.RegionDao;
import com.cdac.exambackup.dao.RoleDao;
import com.cdac.exambackup.dto.*;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.PasswordResetOtp;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.entity.Role;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.AppUserService;
import com.cdac.exambackup.service.EmailService;
import com.cdac.exambackup.util.NullAndBlankUtil;
import com.cdac.exambackup.util.Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    @Value("${role.user.code}")
    String userCode;
    @Value("${role.admin.code}")
    String adminCode;

    static final String USER_NOT_FOUND = "User not found with userId: ";

    static final char[] ALPHANUMERIC = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    static final int OTP_LENGTH = 6;
    static final Random random = new Random();

    final AppUserDao appUserDao;
    final PasswordEncoder passwordEncoder;
    final EmailService emailService;
    final ApplicationContext applicationContext;
    final RegionDao regionDao;
    final RoleDao roleDao;


    public AppUserServiceImpl(BaseDao<AppUser, Long> baseDao, AppUserDao appUserDao, PasswordEncoder passwordEncoder, EmailService emailService, ApplicationContext applicationContext, RegionDao regionDao, RoleDao roleDao) {
        super(baseDao);
        this.appUserDao = appUserDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.applicationContext = applicationContext;
        this.regionDao = regionDao;
        this.roleDao = roleDao;
    }

    @Transactional
    @Override
    public AppUser save(AppUser appUser) {
        // new record entry
        if (appUser.getId() == null) {
            return createNewAppUser(appUser);
        }
        // else update
        if (NullAndBlankUtil.isAllNullOrBlank(appUser.getName(), appUser.getEmail(), appUser.getMobileNumber()) && appUser.getPassword() == null && appUser.getRegionId() == null && appUser.getRole() == null) {
            throw new GenericException("All 'name', 'email', 'mobile', 'roleId','isRegionHead', and 'regionId' cannot be null or empty. At least one of them must be provided");
        }
        AppUser daoAppUser = appUserDao.findById(appUser.getId());

        //  if not found with id, search with userId
        if (daoAppUser == null && appUser.getUserId() != null) {
            daoAppUser = appUserDao.findByUserId(appUser.getUserId());
        }
        if (daoAppUser == null || Boolean.TRUE.equals(daoAppUser.getDeleted())) {
            throw new EntityNotFoundException("User not found.");
        }
        if (Boolean.FALSE.equals(daoAppUser.getActive())) {
            throw new EntityNotFoundException("User with id: " + daoAppUser.getId() + " is not active. Must activate first.");
        }

        if (appUser.getRole() != null) {
            daoAppUser.setRole(appUser.getRole());
        }

        if (appUser.getName() != null) {
            if (appUser.getName().isBlank()) {
                throw new InvalidReqPayloadException("'name' must not be blank.");
            }
            daoAppUser.setName(appUser.getName());
        }

        if (appUser.getUserId() != null) {
            if (appUser.getUserId().isBlank()) {
                throw new InvalidReqPayloadException("'userId' must not be blank.");
            }
            daoAppUser.setUserId(appUser.getUserId());
        }

        if (appUser.getMobileNumber() != null) {
            if (!Util.validateMobileNumber(appUser.getMobileNumber())) {
                throw new InvalidReqPayloadException("Malformed mobile number.");
            }
            daoAppUser.setMobileNumber(appUser.getMobileNumber());
        }
        if (appUser.getEmail() != null) {
            if (!Util.validateEmail(appUser.getEmail())) {
                throw new InvalidReqPayloadException("Malformed email address.");
            }
            daoAppUser.setEmail(appUser.getEmail().trim());
        }

        if (appUser.getIsRegionHead() != null) {
            // allow only one region head
            if (Boolean.TRUE.equals(appUser.getIsRegionHead())) {
                checkIfRegionHeadAlreadyExist(daoAppUser, appUser.getRegionId());
                daoAppUser.setIsRegionHead(true);
            } else {
                daoAppUser.setIsRegionHead(false);
                daoAppUser.setRegionId(null);
            }
        }
        if (appUser.getRegionId() != null) {
            // allow only one region head
            if (Boolean.TRUE.equals(daoAppUser.getIsRegionHead())) {
                checkIfRegionHeadAlreadyExist(daoAppUser, appUser.getRegionId());
            }
            daoAppUser.setRegionId(appUser.getRegionId());
        }
        return appUserDao.save(daoAppUser);
    }

    private AppUser createNewAppUser(AppUser appUser) {
        if (NullAndBlankUtil.isAnyNullOrBlank(appUser.getUserId())) {
            throw new InvalidReqPayloadException("'userId' cannot be null or blank.");
        }
        appUser.setPassword(passwordEncoder.encode(appUser.getUserId()));

        if (appUser.getRole() == null) {
            throw new EntityNotFoundException("Role not found");
        }

        if (NullAndBlankUtil.isNonNullAndBlank(appUser.getName())) {
            throw new InvalidReqPayloadException("'name' cannot be blank.");
        }

        if (appUser.getMobileNumber() != null && !Util.validateMobileNumber(appUser.getMobileNumber())) {
            throw new InvalidReqPayloadException("Malformed mobile number.");
        }

        if (appUser.getEmail() != null && !Util.validateEmail(appUser.getEmail())) {
            throw new InvalidReqPayloadException("Malformed email address.");
        }

        if (appUser.getIsRegionHead() != null && appUser.getIsRegionHead()) {
            if (appUser.getRegionId() == null) {
                throw new InvalidReqPayloadException("'regionId' cannot be null.");
            }
            checkIfRegionHeadAlreadyExist(appUser, appUser.getRegionId());
        } else {
            appUser.setIsRegionHead(false);
        }
        // try adding a new record (more performant)
        // if violation constraint exception is thrown then duplicate exists.
        try {
            return appUserDao.save(appUser);
        } catch (Exception ex) {
            log.info("Error occurred while creating a new AppUser: {}", ex.getMessage());
            throw new InvalidReqPayloadException("Same 'userId' already exists.");
        }
    }

    private void checkIfRegionHeadAlreadyExist(AppUser appUser, Long regionId) {
        Region daoRegion = regionDao.findById(regionId);
        if (daoRegion == null) {
            throw new EntityNotFoundException("Region with id: " + appUser.getRegionId() + " not found");
        }
        appUserDao.getAllRegionHead().forEach(tempAppUser -> {
            if (!tempAppUser.getId().equals(appUser.getId()) && tempAppUser.getRegionId().equals(daoRegion.getId())) {
                throw new InvalidReqPayloadException("Region Head already exists for region id: " + daoRegion.getId());
            }
        });
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
            throw new EntityNotFoundException(USER_NOT_FOUND + passwordChangeDto.userId());
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
            throw new EntityNotFoundException(USER_NOT_FOUND + resetPasswordDto.userId());
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
            throw new EntityNotFoundException(USER_NOT_FOUND + resetPasswordDto.userId());
        }
        daoAppUser.setPassword(passwordEncoder.encode(resetPasswordDto.password()));
        appUserDao.save(daoAppUser);
        appUserDao.deletePasswordResetOtpByUserId(daoPasswordResetOtp.getUserId());
    }

    @Transactional
    @Override
    public AppUser save(AppUserReqDto appUserReqDto) {
        AppUser appUser = new AppUser();
        appUser.setId(appUserReqDto.id());
        appUser.setUserId(appUserReqDto.userId());
        appUser.setName(appUserReqDto.name());
        appUser.setEmail(appUserReqDto.email());
        appUser.setMobileNumber(appUserReqDto.mobileNumber());
        Role daoRole = roleDao.findById(appUserReqDto.roleId()); // could be null
        appUser.setRole(daoRole);
        appUser.setIsRegionHead(appUserReqDto.isRegionHead());
        appUser.setRegionId(appUserReqDto.regionId());
        var appUserService = applicationContext.getBean(AppUserService.class);
        return appUserService.save(appUser);
    }

    @Override
    public PageResDto<List<AppUserResDto>> getAllByPage(Pageable pageable) {
        Page<AppUser> page = appUserDao.getAllByPage(pageable, List.of(userCode, adminCode));
        return new PageResDto<>(pageable.getPageNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), convertAppUsersToAppUserResDto(page.getContent()));
    }

    private List<AppUserResDto> convertAppUsersToAppUserResDto(List<AppUser> appUsers) {
        return appUsers.stream().map(appUser -> new AppUserResDto(appUser.getId(), appUser.getUserId(), appUser.getName(), appUser.getRegionId(), appUser.getMobileNumber(), appUser.getEmail(), appUser.getRole().getId(), appUser.getIsRegionHead(), appUser.getCreatedDate(), appUser.getModifiedDate())).toList();
    }
}
