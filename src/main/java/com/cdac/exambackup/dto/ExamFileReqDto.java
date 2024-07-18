package com.cdac.exambackup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

public record ExamFileReqDto(MultipartFile file, String examCentreCode, Long examSlotId, Long fileTypeId, Long id,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
                             LocalDateTime examDate) {
}
