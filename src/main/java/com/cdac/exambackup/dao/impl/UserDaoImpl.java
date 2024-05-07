package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.UserDao;
import com.cdac.exambackup.dao.repo.UserRepository;
import com.cdac.exambackup.entity.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UserDaoImpl extends AbstractBaseDao<User, Long> implements UserDao {
    @Autowired
    UserRepository userRepository;

    @Override
    public JpaRepository<User, Long> getRepository() {
        return this.userRepository;
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public User findByUserId(String userId) {
        return this.userRepository.findFirstByUserId(userId);
    }
}
