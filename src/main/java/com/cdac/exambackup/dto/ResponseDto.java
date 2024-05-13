package com.cdac.exambackup.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author athisii
 * @version 1.0
 * @since 5/3/24
 */

@Schema(name = "ResponseDto", description = "Response object for every API.")
public record ResponseDto<T>(
        @Schema(description = "Message received from API") String message,
        @Schema(description = "true on success, false on failure") boolean status,
        @Schema(description = "Data received from API. Its an generic object can return data of any type") T data) {
    public ResponseDto(String message, T data) {
        this(message, true, data);
    }

    public ResponseDto(String message, boolean status) {
        this(message, status, null);
    }
}
