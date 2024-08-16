package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.ExamDateReqDto;
import com.cdac.exambackup.entity.ExamDate;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamDateService extends BaseService<ExamDate, Long> {
    ExamDate save(ExamDateReqDto examDateReqDto);

}
