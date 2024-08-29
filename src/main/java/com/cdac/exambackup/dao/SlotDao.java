package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface SlotDao extends BaseDao<Slot, Long> {
    List<Slot> findByCodeOrName(String code, String name);

    Page<Slot> getAllByPage(Pageable pageable);

}
