package com.cdac.exambackup.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamCentreReqDto(Long id, String code, String name, String regionName, String mobileNumber, String email,
                               List<ExamDateSlot> examDateSlots) {
}
