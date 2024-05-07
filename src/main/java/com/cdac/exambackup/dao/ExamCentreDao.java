package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.ExamCentre;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */


public interface ExamCentreDao extends BaseDao<ExamCentre, Long> {
    ExamCentre findByCode(String code);
}
