package com.cdac.exambackup.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

public record ExamFileReqDto(MultipartFile file, Long examCentreId, Long examSlotId, Long fileTypeId, Long id,
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm a") LocalDateTime examDate) {
}
