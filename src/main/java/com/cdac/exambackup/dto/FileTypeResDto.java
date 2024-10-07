package com.cdac.exambackup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * @author athisii
 * @version 1.0
 * @since 10/7/24
 */

public record FileTypeResDto(Long id, String code, String name, Long fileExtensionId,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") LocalDateTime createdDate,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") LocalDateTime modifiedDate) {
}
