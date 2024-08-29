package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.ExamDateReqDto;
import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.ExamDate;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamDateService extends BaseService<ExamDate, Long> {
    ExamDate save(ExamDateReqDto examDateReqDto);

    PageResDto<List<ExamDate>> getByExamCentreId(Long examCentreId, Pageable pageable);
}
