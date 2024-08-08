package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.ExamCentre;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamCentreService extends BaseService<ExamCentre, Long> {
    PageResDto<List<ExamCentre>> getByCodeOrNameOrRegionId(String code, String name, Long regionId, Pageable pageable);
}
