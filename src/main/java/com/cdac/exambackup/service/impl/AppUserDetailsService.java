package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.entity.AppUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
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
    @Autowired
    AppUserDao appUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserDao.findByUserId(username);
        if (appUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return User.builder()
                .username(appUser.getUserId())
                .password(appUser.getPassword())
                .roles(appUser.getRole().getName())
                .disabled(!appUser.getActive())
                .accountLocked(appUser.isLocked())
                .build();
    }
}
