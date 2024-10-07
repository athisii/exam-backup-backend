package com.cdac.exambackup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamCentreResDto(Long id, String code, String name, String regionName, String mobileNumber, String email,
                               Integer totalFileCount,
                               Integer uploadedFileCount, Set<ExamDateSlot> examDateSlots,
                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") LocalDateTime createdDate,
                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") LocalDateTime modifiedDate) {
}
