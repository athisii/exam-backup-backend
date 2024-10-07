package com.cdac.exambackup.dto;

import java.util.Set;

/**
 * @author athisii
 * @version 1.0
 * @since 10/3/24
 */

public record ExamCentreSlotUpdateReqDto(Set<Long> examCentreIds, Set<Long> examDateIds, Set<Long> slotIds) {
}
