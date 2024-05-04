package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.BaseDao;
import com.cdac.exambackup.dao.UserDao;
import com.cdac.exambackup.entity.User;
import com.cdac.exambackup.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
