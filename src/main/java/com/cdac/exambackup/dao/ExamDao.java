package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.Exam;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamDao extends BaseDao<Exam, Long> {
    Exam findByExamCentreAndExamDate(ExamCentre examCentre, ExamDate examDate);

    Exam findByExamCentreIdAndExamDateId(Long examCentreId, Long examDateId);

    Page<Exam> getByExamCentreId(Long examCentreId, Pageable pageable);

    List<Exam> findByExamCentreId(Long examCentreId);

    List<Long> findIdsByExamCentreId(Long examCentreId);

    void deleteByExamCentre(ExamCentre examCentre);
}
