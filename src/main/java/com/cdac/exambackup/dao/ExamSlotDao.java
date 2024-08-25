package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.ExamSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamSlotDao extends BaseDao<ExamSlot, Long> {
    List<ExamSlot> findByCodeOrName(Integer code, String name);

    Page<ExamSlot> getAllByPage(Pageable pageable);

}
