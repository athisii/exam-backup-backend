package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.AppUser;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface AppUserDao extends BaseDao<AppUser, Long> {
    AppUser findByUserId(String userId);
}
