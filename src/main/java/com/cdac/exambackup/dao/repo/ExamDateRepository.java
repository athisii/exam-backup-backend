package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.ExamDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface ExamDateRepository extends JpaRepository<ExamDate, Long> {
}
