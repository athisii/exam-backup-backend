package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.User;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface UserDao extends BaseDao<User, Long> {
    User findByUserId(String userId);
}
