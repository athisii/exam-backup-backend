package com.cdac.exambackup.dto;

import java.util.Set;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

public record ExamDateSlot(Long examDateId, Set<Long> slotIds) {
}
