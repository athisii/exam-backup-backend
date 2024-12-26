package com.cdac.exambackup.security;

import com.cdac.exambackup.dto.IdentityContext;
import com.cdac.exambackup.exception.ForbiddenException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author athisii
 * @version 1.0
 * @since 10/15/24
 */

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityUtil {
    private static final String UNAUTHORIZED_MSG = "You are unauthorized to perform this action.";
    @Value("${role.admin.code}")
    Long adminCode;
    @Value("${role.staff.code}")
    Long staffCode;
    @Value("${role.user.code}")
    Long userCode;

    @Autowired
    IdentityContext identityContext;

    public void hasWritePermission() {
        if (!identityContext.getPermissions().contains(adminCode)) {
            throw new ForbiddenException(UNAUTHORIZED_MSG);
        }
    }

    public void hasReadPermission() {
        if (!(identityContext.getPermissions().contains(adminCode) || identityContext.getPermissions().contains(staffCode) || identityContext.getPermissions().contains(userCode))) {
            throw new ForbiddenException(UNAUTHORIZED_MSG);
        }
    }

    public void isAdminOrStaff() {
        if (!(identityContext.getPermissions().contains(adminCode) || identityContext.getPermissions().contains(staffCode))) {
            throw new ForbiddenException(UNAUTHORIZED_MSG);
        }
    }
}
