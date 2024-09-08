package com.cdac.exambackup.dto;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

public record ExamDateSlot(Long examDateId, List<Long> slotIds) {
}
