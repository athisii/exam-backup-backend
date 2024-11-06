package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.dao.repo.AppUserRepository;
import com.cdac.exambackup.dao.repo.PasswordResetOtpRepository;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.PasswordResetOtp;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AppUserDaoImpl extends AbstractBaseDao<AppUser, Long> implements AppUserDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    PasswordResetOtpRepository passwordResetOtpRepository;

    @Override
    public JpaRepository<AppUser, Long> getRepository() {
        return this.appUserRepository;
    }

    @Override
    public Class<AppUser> getEntityClass() {
        return AppUser.class;
    }

    @Override
    public AppUser findByUserId(String userId) {
        return this.appUserRepository.findFirstByUserIdAndDeletedFalseAndActiveTrue(userId);
    }

    @Override
    public void resetPassword(PasswordResetOtp passwordResetOtp) {
        passwordResetOtpRepository.save(passwordResetOtp);
    }

    @Override
    public PasswordResetOtp findPasswordResetOtp(String userId) {
        return this.passwordResetOtpRepository.findFirstByUserIdAndDeletedFalse(userId);
    }

    @Override
    public void deletePasswordResetOtpByUserId(String userId) {
        this.passwordResetOtpRepository.deleteByUserId(userId);
    }

    @Override
    public Page<AppUser> getAllByPage(Pageable pageable, List<String> roleCodes) {
        try {
            return this.appUserRepository.findByDeletedFalse(pageable, roleCodes);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public List<AppUser> getAllRegionHead() {
        return this.appUserRepository.findByIsRegionHeadTrueAndDeletedFalse();
    }
}
