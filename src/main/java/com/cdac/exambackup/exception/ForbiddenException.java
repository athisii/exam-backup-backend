package com.cdac.exambackup.exception;

import lombok.Getter;

/**
 * @author athisii
 * @version 1.0
 * @since 5/3/24
 */

@Getter
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

}
