package com.cdac.exambackup.exception;

import lombok.Getter;

/**
 * @author athisii
 * @version 1.0
 * @since 5/3/24
 */

@Getter
public class InvalidReqPayloadException extends RuntimeException {

    public InvalidReqPayloadException(String message) {
        super(message);
    }

}
