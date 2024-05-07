package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.ExamSlot;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamSlotDao extends BaseDao<ExamSlot, Long> {
    List<ExamSlot> findByCodeOrName(Integer code, String name);

}
