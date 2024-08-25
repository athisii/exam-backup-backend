package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.ExamSlot;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamSlotService extends BaseService<ExamSlot, Long> {
    PageResDto<List<ExamSlot>> getAllByPage(Pageable pageable);
}
