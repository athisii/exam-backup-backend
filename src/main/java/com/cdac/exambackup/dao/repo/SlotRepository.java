package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByCodeOrNameIgnoreCaseAndDeletedFalse(String code, String name);
    @Query("SELECT es FROM Slot es WHERE es.deleted = false ORDER BY CAST(es.code AS INTEGER) ASC")
    Page<Slot> findByDeletedFalse(Pageable pageable);
}
