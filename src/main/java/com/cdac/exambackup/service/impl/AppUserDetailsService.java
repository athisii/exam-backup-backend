package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.AppUserDetails;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author athisii
 * @version 1.0
 * @since 5/13/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AppUserDetailsService implements UserDetailsService {
    final AppUserDao appUserDao;

    public AppUserDetailsService(AppUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserDao.findByUserId(username);
        if (appUser == null || Boolean.TRUE.equals(appUser.getDeleted())) {
            throw new UsernameNotFoundException(username);
        }
        return new AppUserDetails(appUser);
    }
}
