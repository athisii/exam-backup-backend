package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamDate;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamDateDao extends BaseDao<ExamDate, Long> {
    List<ExamDate> findByExamCentre(ExamCentre examCentre);
}
