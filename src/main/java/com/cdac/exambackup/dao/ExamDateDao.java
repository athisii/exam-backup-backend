package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.ExamDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamDateDao extends BaseDao<ExamDate, Long> {
    Page<ExamDate> getAllByPage(Pageable pageable);
}
