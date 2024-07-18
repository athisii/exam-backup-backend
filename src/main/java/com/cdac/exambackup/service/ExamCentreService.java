package com.cdac.exambackup.service;

import com.cdac.exambackup.entity.ExamCentre;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamCentreService extends BaseService<ExamCentre, Long> {
    ExamCentre getByCode(String code);
}
