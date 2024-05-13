package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.AppUserService;
import com.cdac.exambackup.util.Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AppUserServiceImpl extends AbstractBaseService<AppUser, Long> implements AppUserService {
    @Autowired
    AppUserDao appUserDao;

    public AppUserServiceImpl(BaseDao<AppUser, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public AppUser save(AppUser appUserDto) {
        // for admin
        if ("000".equals(appUserDto.getUserId()) && appUserDao.count() == 0L) {
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
        if (daoAppUser == null) {
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
            daoAppUser.setEmail(appUserDto.getEmail());
        }
        return appUserDao.save(daoAppUser);
    }
}