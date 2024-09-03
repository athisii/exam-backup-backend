package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.dao.repo.AppUserRepository;
import com.cdac.exambackup.entity.AppUser;
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
public class AppUserDaoImpl extends AbstractBaseDao<AppUser, Long> implements AppUserDao {
    @Autowired
    AppUserRepository appUserRepository;

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
}
