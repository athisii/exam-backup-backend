package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.ExamSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface ExamSlotRepository extends JpaRepository<ExamSlot, Long> {
    List<ExamSlot> findByCodeOrNameIgnoreCaseAndDeletedFalse(String code, String name);

    Page<ExamSlot> findByDeletedFalse(Pageable pageable);
}
