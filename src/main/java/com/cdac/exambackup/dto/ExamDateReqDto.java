package com.cdac.exambackup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

public record ExamDateReqDto(Long id, Long examCentreId, Set<Long> examSlotIds,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
                             LocalDateTime examDate) {
}
