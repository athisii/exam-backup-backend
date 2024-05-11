package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.UserDao;
import com.cdac.exambackup.entity.User;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.UserService;
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
public class UserServiceImpl extends AbstractBaseService<User, Long> implements UserService {
    @Autowired
    UserDao userDao;

    public UserServiceImpl(BaseDao<User, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public User save(User userDto) {
        if (userDto.getId() == null && userDto.getUserId() == null) {
            throw new GenericException("Both id and userId can not be null or empty");
        }
        if ((userDto.getEmail() == null || userDto.getEmail().isBlank()) && (userDto.getMobileNumber() == null || userDto.getMobileNumber().isBlank())) {
            throw new GenericException("Both email and mobile can not be null or empty. At least one of them must be provided");
        }
        User daoUser = null;
        if (userDto.getId() != null) {
            daoUser = userDao.findById(userDto.getId());
        }
        //  if not found with id, search with userId
        if (daoUser == null && userDto.getUserId() != null) {
            daoUser = userDao.findByUserId(userDto.getUserId());
        }
        if (daoUser == null) {
            throw new EntityNotFoundException("User not found.");
        }
        if (Boolean.FALSE.equals(daoUser.getActive())) {
            throw new EntityNotFoundException("ExamCentre with id: " + daoUser.getId() + " is not active. Must activate first.");
        }
        if (userDto.getMobileNumber() != null) {
            if (!Util.validateMobileNumber(userDto.getMobileNumber())) {
                throw new GenericException("Malformed mobile number.");
            }
            daoUser.setMobileNumber(userDto.getMobileNumber());
        }
        if (userDto.getEmail() != null) {
            if (!Util.validateEmail(userDto.getEmail())) {
                throw new GenericException("Malformed email address.");
            }
            daoUser.setEmail(userDto.getEmail());
        }
        return userDao.save(daoUser);
    }
}
