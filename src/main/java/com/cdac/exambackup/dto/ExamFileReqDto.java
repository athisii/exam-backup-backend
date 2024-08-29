package com.cdac.exambackup.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

public record ExamFileReqDto(MultipartFile file, Long examCentreId, Long slotId, Long fileTypeId, Long id,
                             Long examDateId) {
}
