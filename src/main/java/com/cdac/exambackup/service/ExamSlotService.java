package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.ExamSlotReqDto;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.ExamSlot;
import org.springframework.data.domain.Page;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamSlotService extends BaseService<ExamSlot, Long> {
    ExamSlot save(ExamSlotReqDto examSlotReqDto);
}
