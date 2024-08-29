package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.Slot;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface SlotService extends BaseService<Slot, Long> {
    PageResDto<List<Slot>> getAllByPage(Pageable pageable);

    PageResDto<List<Slot>> getByExamCentreIdAndExamDateId(Long examCentreId, Long examDateId, Pageable pageable);
}
