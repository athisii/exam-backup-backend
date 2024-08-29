package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.ExamReqDto;
import com.cdac.exambackup.entity.Exam;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamService extends BaseService<Exam, Long> {
    Exam save(ExamReqDto examReqDto);

    Exam getByExamCentreIdAndExamDateId(Long examCentreId, Long examDateId);
}
