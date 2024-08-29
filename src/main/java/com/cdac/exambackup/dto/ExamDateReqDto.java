package com.cdac.exambackup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

public record ExamDateReqDto(Long id,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                             LocalDate date) {
}
